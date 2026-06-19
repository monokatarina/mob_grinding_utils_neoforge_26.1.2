package mob_grinding_utils.blocks;

import mob_grinding_utils.ModTags;
import mob_grinding_utils.events.DirtSpawnEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.util.TriState;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockDelightfulDirt extends BlockDirtSpawner {

	public BlockDelightfulDirt(Block.Properties properties) {
		super(properties);
	}

	public boolean shouldSnowCap(LevelAccessor level, BlockPos pos) {
		long timeOfDay = level.getGameTime() % 24000L;
		return level.canSeeSkyFromBelowWater(pos) && timeOfDay >= 13000L && timeOfDay <= 23000L;
	}

	public boolean shouldSpawnMob(LevelAccessor level, BlockPos pos) {
		return level.getMaxLocalRawBrightness(pos.above()) >= 10 && level.getBlockState(pos.above()).isAir();
	}

	@Override
	public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
		if (shouldSnowCap(level, pos) || shouldSpawnMob(level, pos))
			level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 5, 20));
	}

	@Nonnull
	@Override
	public BlockState updateShape(@Nonnull BlockState stateIn, @Nonnull LevelReader level, @Nonnull ScheduledTickAccess ticks, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull BlockPos facingPos, @Nonnull BlockState facingState, @Nonnull RandomSource random) {
		if (level instanceof LevelAccessor accessor && (shouldSnowCap(accessor, pos) || shouldSpawnMob(accessor, pos)))
			accessor.scheduleTick(pos, this, Mth.nextInt(random, 5, 20));
		return super.updateShape(stateIn, level, ticks, pos, facing, facingPos, facingState, random);
	}

	@Override
	public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
		if (shouldSnowCap(level, pos) || shouldSpawnMob(level, pos))
			level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 5, 20));
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		if (shouldSnowCap(level, pos)) {
			BlockPos posUp = pos.above();
			BlockState blockstate = Blocks.SNOW.defaultBlockState();
			if (level.getBlockState(posUp).isAir() && blockstate.canSurvive(level, posUp))
				level.setBlock(posUp, blockstate, 11);
		}

		if (!shouldSnowCap(level, pos) && shouldSpawnMob(level, pos)) {
			AABB areaToCheck = new AABB(pos).inflate(5, 2, 5);
			int entityCount = level.getEntitiesOfClass(Mob.class, areaToCheck, entity -> entity.getType().getCategory() == MobCategory.CREATURE).size();
			if (entityCount < 8)
				spawnMob(level, pos);
		}
	}

	@Override
	public TriState canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nonnull BlockState plant) {
		return TriState.TRUE;
	}

	public void spawnMob(ServerLevel level, BlockPos pos) {
		Holder<Biome> biomeHolder = level.getBiome(pos);
		Biome biome = !biomeHolder.is(ModTags.Biomes.PASSIVE_OVERRIDE) ? biomeHolder.value() : level.registryAccess().lookup(Registries.BIOME)
				.flatMap(reg -> reg.getOptional(Biomes.PLAINS))
				.orElseGet(biomeHolder::value);

		List<Weighted<SpawnerData>> spawns = biome.getMobSettings().getMobs(MobCategory.CREATURE).unwrap();
		if (spawns.isEmpty())
			return;

		SpawnerData data = spawns.get(level.getRandom().nextInt(spawns.size())).value();
		EntityType<?> type = data.type();
		if (type.builtInRegistryHolder().is(ModTags.Entities.NO_DIRT_SPAWN) || type.builtInRegistryHolder().is(ModTags.Entities.NO_DELIGHTFUL_SPAWN))
			return;

		Mob entity = (Mob) type.create(level, EntitySpawnReason.NATURAL);
		if (entity == null)
			return;
		entity.setPos(pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
		if (!checkSpawnPosition(entity, level, EntitySpawnReason.NATURAL))
			return;

		if (level.getEntities(entity.getType(), entity.getBoundingBox(), EntitySelector.ENTITY_STILL_ALIVE).isEmpty() && level.noCollision(entity)) {
			TriState result = DirtSpawnEvent.checkEvent(entity, level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, DirtSpawnEvent.DirtType.DELIGHTFUL);
			if (result == TriState.FALSE)
				return;
			EventHooks.finalizeMobSpawn(entity, level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.NATURAL, null);
			level.addFreshEntity(entity);
		}
	}

	@Override
	public boolean isFlammable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) {
		return true;
	}

	@Override
	public int getFlammability(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction face) {
		return 200;
	}

	@Override
	public boolean isFireSource(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Direction side) {
		return side == Direction.UP;
	}

	@Override
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		if (level.getGameTime() % 3 == 0 && level.getBlockState(pos.above()).isAir()) {
			for (int i = 0; i < 4; ++i) {
				double d0 = pos.getX();
				double d1 = pos.getY() + 1D;
				double d2 = pos.getZ();
				double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
				double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
				double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
				level.addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL, d0 + d3, d1 + d4, d2 + d5, 0D, 0D, 0D);
			}
		}

		if (shouldSnowCap(level, pos) || shouldSpawnMob(level, pos)) {
			level.scheduleTick(pos, this, Mth.nextInt(rand, 5, 20));
		}
	}
}

