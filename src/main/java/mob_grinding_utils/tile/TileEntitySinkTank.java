package mob_grinding_utils.tile;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.network.TapParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class TileEntitySinkTank extends TileEntityTank {

	private static final int XP_FLUID_PER_POINT = 20;

	public TileEntitySinkTank(
			BlockPos pos,
			BlockState state
	) {
		super(
				ModBlocks.TANK_SINK.getTileEntityType(),
				pos,
				state
		);
	}

	public static <T extends BlockEntity> void serverTick(
			Level level,
			BlockPos worldPosition,
			BlockState blockState,
			T blockEntity
	) {
		if (blockEntity instanceof TileEntitySinkTank tile) {
			FluidResource storedFluid =
					tile.tank.getResource(0);

			FluidResource xpFluid =
					FluidResource.of(
							ModBlocks.FLUID_XP.get()
					);

			if (storedFluid.isEmpty()
					|| storedFluid.equals(xpFluid)) {
				tile.captureDroppedXP();
			}

			TileEntityTank.serverTick(
					level,
					worldPosition,
					blockState,
					blockEntity
			);
		}
	}

	public boolean captureDroppedXP() {
		Level currentLevel = getLevel();

		if (!(currentLevel instanceof ServerLevel serverLevel)) {
			return false;
		}

		FluidResource xpFluid =
				FluidResource.of(
						ModBlocks.FLUID_XP.get()
				);

		int storedAmount =
				tank.getAmountAsInt(0);

		int capacity =
				tank.getCapacityAsInt(
						0,
						xpFluid
				);

		if (storedAmount + XP_FLUID_PER_POINT > capacity) {
			return false;
		}

		for (Player player : getCaptureXP(
				serverLevel,
				getBlockPos().getX() + 0.5D,
				getBlockPos().getY() + 0.5D,
				getBlockPos().getZ() + 0.5D
		)) {
			if (player.isSpectator()) {
				continue;
			}

			int xpAmount = getPlayerXP(player);

			if (xpAmount <= 0) {
				continue;
			}

			try (Transaction transaction =
						 Transaction.openRoot()) {
				int inserted = tank.insert(
						xpFluid,
						XP_FLUID_PER_POINT,
						transaction
				);

				if (inserted
						!= XP_FLUID_PER_POINT) {
					continue;
				}

				transaction.commit();
			}

			addPlayerXP(player, -1);

			serverLevel.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.BOTTLE_FILL,
					SoundSource.NEUTRAL,
					0.1F,
					0.5F * (
							(
									serverLevel.getRandom().nextFloat()
											- serverLevel.getRandom().nextFloat()
							) * 0.7F + 1.8F
					)
			);

			PacketDistributor.sendToPlayersNear(
					serverLevel,
					null,
					getBlockPos().getX(),
					getBlockPos().getY(),
					getBlockPos().getZ(),
					30,
					new TapParticlePacket(
							getBlockPos().above()
					)
			);

			return true;
		}

		return false;
	}

	public List<Player> getCaptureXP(
			Level level,
			double x,
			double y,
			double z
	) {
		return level.getEntitiesOfClass(
				Player.class,
				new AABB(
						x - 0.45D,
						y - 0.5D,
						z - 0.45D,
						x + 0.45D,
						y + 1.03D,
						z + 0.45D
				),
				EntitySelector.ENTITY_STILL_ALIVE
		);
	}

	public static void addPlayerXP(
			Player player,
			int amount
	) {
		int experience =
				Math.max(
						0,
						getPlayerXP(player) + amount
				);

		player.totalExperience = experience;
		player.experienceLevel =
				getLevelForExperience(experience);

		int experienceForLevel =
				getExperienceForLevel(
						player.experienceLevel
				);

		int neededForNextLevel =
				player.getXpNeededForNextLevel();

		player.experienceProgress =
				neededForNextLevel <= 0
						? 0.0F
						: (float) (
						experience
						- experienceForLevel
				) / neededForNextLevel;
	}

	public static int getPlayerXP(Player player) {
		return getExperienceForLevel(
				player.experienceLevel
		) + (int) (
				player.experienceProgress
						* player.getXpNeededForNextLevel()
		);
	}

	public static int getLevelForExperience(
			int experience
	) {
		int level = 0;

		while (getExperienceForLevel(level)
				<= experience) {
			level++;
		}

		return level - 1;
	}

	public static int getExperienceForLevel(
			int level
	) {
		if (level == 0) {
			return 0;
		}

		if (level < 17) {
			return (int) (
					Math.pow(level, 2)
							+ 6 * level
			);
		}

		if (level < 32) {
			return (int) (
					2.5D * Math.pow(level, 2)
							- 40.5D * level
							+ 360.0D
			);
		}

		return (int) (
				4.5D * Math.pow(level, 2)
						- 162.5D * level
						+ 2220.0D
		);
	}
}

