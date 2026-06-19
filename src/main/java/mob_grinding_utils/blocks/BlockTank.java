package mob_grinding_utils.blocks;

import com.mojang.serialization.MapCodec;
import mob_grinding_utils.tile.TileEntityTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.Nullable;

public class BlockTank extends BaseEntityBlock {

	public static final MapCodec<BlockTank> CODEC =
			simpleCodec(BlockTank::new);

	public BlockTank(Block.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(
			BlockPos pos,
			BlockState state
	) {
		return new TileEntityTank(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType
	) {
		return level.isClientSide()
				? null
				: TileEntityTank::serverTick;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult useItemOn(
			ItemStack stack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hit
	) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof TileEntityTank)) {
			return InteractionResult.PASS;
		}

		ResourceHandler<FluidResource> handler =
				level.getCapability(
						Capabilities.Fluid.BLOCK,
						pos,
						state,
						blockEntity,
						hit.getDirection()
				);

		if (handler == null) {
			return InteractionResult.PASS;
		}

		if (!stack.isEmpty()
				&& FluidUtil.interactWithFluidHandler(
				player,
				hand,
				pos,
				handler,
				null
		)) {
			return InteractionResult.SUCCESS;
		}

		showTankContents(player, handler);
		return InteractionResult.SUCCESS;
	}

	private static void showTankContents(
			Player player,
			ResourceHandler<FluidResource> handler
	) {
		if (handler.size() <= 0) {
			player.sendSystemMessage(
					Component.literal("Empty: 0/0")
			);
			return;
		}

		FluidResource resource = handler.getResource(0);
		int amount = handler.getAmountAsInt(0);
		int capacity = handler.getCapacityAsInt(
				0,
				resource.isEmpty()
						? FluidResource.EMPTY
						: resource
		);

		if (resource.isEmpty() || amount <= 0) {
			player.sendSystemMessage(
					Component.literal(
							"Empty: 0/" + capacity
					)
			);
			return;
		}

		var fluidStack = resource.toStack(amount);

		player.sendSystemMessage(
				Component.literal(
						fluidStack.getHoverName().getString()
								+ ": "
								+ amount
								+ "/"
								+ capacity
				)
		);
	}
}

