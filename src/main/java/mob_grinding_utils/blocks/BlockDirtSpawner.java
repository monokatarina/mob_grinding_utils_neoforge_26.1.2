package mob_grinding_utils.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class BlockDirtSpawner extends Block {
    private static final String DIRT_SPAWNED_TAG = "mob_grinding_utils.dirt_spawned";
    protected static final int LOCAL_MOB_LIMIT = 8;
    protected static final int AREA_MOB_LIMIT = 50;
    protected static final int AREA_LIMIT_HORIZONTAL_RADIUS = 16;
    protected static final int AREA_LIMIT_VERTICAL_RADIUS = 8;
    protected static final int DIRT_MOB_LIFETIME_TICKS = 600;

    public BlockDirtSpawner(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void tick(@Nonnull BlockState pState, @Nonnull ServerLevel pLevel, @Nonnull BlockPos pPos, @Nonnull RandomSource pRandom) {
        randomTick(pState, pLevel, pPos, pRandom);
    }

    public static boolean checkSpawnPosition(Mob mob, ServerLevelAccessor level, EntitySpawnReason spawnReason) {
        return mob.checkSpawnRules(level, spawnReason) && mob.checkSpawnObstruction(level); // Yoinked from EventHooks.checkSpawnPosition, but without the event post.
    }

    protected static AABB localSpawnArea(BlockPos pos) {
        return new AABB(pos).inflate(5, 2, 5);
    }

    protected static AABB areaLimit(BlockPos pos) {
        return new AABB(pos).inflate(AREA_LIMIT_HORIZONTAL_RADIUS, AREA_LIMIT_VERTICAL_RADIUS, AREA_LIMIT_HORIZONTAL_RADIUS);
    }

    protected static int countNearbyMobs(ServerLevel level, BlockPos pos, MobCategory category) {
        return level.getEntitiesOfClass(Mob.class, areaLimit(pos), entity -> EntitySelector.ENTITY_STILL_ALIVE.test(entity)
                && entity.getType().getCategory() == category).size();
    }

    protected static void discardExpiredDirtMobs(ServerLevel level, BlockPos pos, MobCategory category) {
        level.getEntitiesOfClass(Mob.class, areaLimit(pos), entity -> EntitySelector.ENTITY_STILL_ALIVE.test(entity)
                && entity.getType().getCategory() == category
                && entity.entityTags().contains(DIRT_SPAWNED_TAG)
                && entity.tickCount > DIRT_MOB_LIFETIME_TICKS).forEach(Mob::discard);
    }

    protected static boolean canSpawnMoreDirtMobs(ServerLevel level, BlockPos pos, MobCategory category) {
        return countNearbyMobs(level, pos, category) < AREA_MOB_LIMIT;
    }

    protected static void limitDirtSlimeSize(Mob entity, RandomSource random) {
        if (entity instanceof Slime slime) {
            slime.setSize(random.nextBoolean() ? 1 : 2, true);
        }
    }

    protected static void prepareDirtSpawnedMob(Mob entity, RandomSource random) {
        limitDirtSlimeSize(entity, random);
        entity.setNoAi(true);
        entity.addTag(DIRT_SPAWNED_TAG);
    }
}

