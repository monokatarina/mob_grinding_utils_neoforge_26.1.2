package mob_grinding_utils.tile;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModTags;
import mob_grinding_utils.blocks.BlockXPTap;
import mob_grinding_utils.entity.EntityXPOrbFalling;
import mob_grinding_utils.network.TapParticlePacket;
import mob_grinding_utils.util.CapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

public class TileEntityXPTap extends BlockEntity {

	private static final int FLUID_PER_XP_UNIT = 20;

	public boolean active;

	public TileEntityXPTap(
			BlockPos pos,
			BlockState state
	) {
		super(
				ModBlocks.XP_TAP.getTileEntityType(),
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
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!(blockEntity instanceof TileEntityXPTap tap)
				|| !tap.active) {
			return;
		}

		if (serverLevel.getGameTime() % 3 != 0) {
			return;
		}

		Direction facing =
				blockState.getValue(BlockXPTap.FACING);

		BlockPos tankPos =
				worldPosition.relative(facing.getOpposite());

		BlockEntity tankBlockEntity =
				serverLevel.getBlockEntity(tankPos);

		if (tankBlockEntity == null) {
			return;
		}

		Optional<ResourceHandler<FluidResource>> handlerOptional =
				CapHelper.getFluidHandler(
						serverLevel,
						tankPos,
						facing
				);

		if (handlerOptional.isEmpty()) {
			return;
		}

		ResourceHandler<FluidResource> handler =
				handlerOptional.get();

		if (handler.size() <= 0) {
			return;
		}

		FluidResource resource =
				handler.getResource(0);

		int storedAmount =
				handler.getAmountAsInt(0);

		if (resource.isEmpty()
				|| storedAmount < FLUID_PER_XP_UNIT) {
			return;
		}

		var fluidStack =
				FluidUtil.getStack(handler, 0);

		if (fluidStack.isEmpty()
				|| !fluidStack.typeHolder()
				.is(ModTags.Fluids.EXPERIENCE)) {
			return;
		}

		int xpAmount =
				EntityXPOrbFalling.getExperienceValue(
						Math.min(
								20,
								storedAmount / FLUID_PER_XP_UNIT
						)
				);

		if (xpAmount <= 0) {
			return;
		}

		int amountToDrain =
				xpAmount * FLUID_PER_XP_UNIT;

		try (Transaction transaction = Transaction.openRoot()) {
			int extracted =
					handler.extract(
							0,
							resource,
							amountToDrain,
							transaction
					);

			if (extracted != amountToDrain) {
				return;
			}

			transaction.commit();
		}

		tap.spawnXP(
				serverLevel,
				worldPosition,
				xpAmount,
				tankBlockEntity
		);

		PacketDistributor.sendToPlayersNear(
				serverLevel,
				null,
				worldPosition.getX(),
				worldPosition.getY(),
				worldPosition.getZ(),
				30,
				new TapParticlePacket(worldPosition)
		);
	}

	public void spawnXP(
			Level level,
			BlockPos pos,
			int xp,
			BlockEntity tankBlockEntity
	) {
		tankBlockEntity.setChanged();

		EntityXPOrbFalling orb =
				new EntityXPOrbFalling(
						level,
						pos.getX() + 0.5D,
						pos.getY() - 0.125D,
						pos.getZ() + 0.5D,
						xp
				);

		level.addFreshEntity(orb);
	}

	public void setActive(boolean active) {
		this.active = active;
		setChanged();

		Level level = getLevel();

		if (level == null) {
			return;
		}

		BlockState state =
				level.getBlockState(worldPosition);

		level.sendBlockUpdated(
				worldPosition,
				state,
				state,
				3
		);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean("active", active);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		active = input.getBooleanOr("active", false);
	}

	@Override
	public CompoundTag getUpdateTag(
			HolderLookup.Provider registries
	) {
		return saveCustomOnly(registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}

