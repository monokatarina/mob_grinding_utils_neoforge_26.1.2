package mob_grinding_utils.blocks;

import com.mojang.serialization.MapCodec;
import mob_grinding_utils.components.FluidContents;
import mob_grinding_utils.components.MGUComponents;
import mob_grinding_utils.tile.TileEntityXPSolidifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BlockXPSolidifier extends BaseEntityBlock {

	public static final MapCodec<BlockXPSolidifier> CODEC =
			simpleCodec(BlockXPSolidifier::new);

	public static final EnumProperty<Direction> FACING =
			HorizontalDirectionalBlock.FACING;

	public BlockXPSolidifier(Properties properties) {
		super(properties);

		registerDefaultState(
				stateDefinition.any()
						.setValue(FACING, Direction.NORTH)
		);
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
		return new TileEntityXPSolidifier(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType
	) {
		return TileEntityXPSolidifier::tick;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction =
				context.getHorizontalDirection().getOpposite();

		return defaultBlockState()
				.setValue(FACING, direction);
	}

	@Override
	public BlockState playerWillDestroy(
			Level level,
			BlockPos pos,
			BlockState state,
			Player player
	) {
		if (!level.isClientSide()
				&& !player.getAbilities().instabuild) {

			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof TileEntityXPSolidifier entity) {
				dropHandlerContents(
						level,
						pos,
						entity.inputSlots
				);

				dropHandlerContents(
						level,
						pos,
						entity.outputSlot
				);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	private static void dropHandlerContents(
			Level level,
			BlockPos pos,
			ItemStacksResourceHandler handler
	) {
		for (int slot = 0; slot < handler.size(); slot++) {
			ItemStack stack = ItemUtil.getStack(handler, slot);

			if (!stack.isEmpty()) {
				Containers.dropItemStack(
						level,
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						stack.copy()
				);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(
			StateDefinition.Builder<Block, BlockState> builder
	) {
		builder.add(FACING);
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

		if (!(blockEntity instanceof TileEntityXPSolidifier solidifier)) {
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

		if (handler != null
				&& !stack.isEmpty()
				&& FluidUtil.interactWithFluidHandler(
				player,
				hand,
				pos,
				handler,
				null
		)) {
			return InteractionResult.SUCCESS;
		}

		player.openMenu(solidifier, pos);
		return InteractionResult.SUCCESS;
	}

	public void appendHoverText(
			ItemStack stack,
			Item.TooltipContext context,
			List<Component> tooltipComponents,
			TooltipFlag tooltipFlag
	) {
		if (!stack.has(MGUComponents.FLUID)) {
			return;
		}

		FluidStack fluid =
				stack.getOrDefault(
						MGUComponents.FLUID,
						FluidContents.EMPTY
				).get();

		if (fluid.isEmpty()) {
			return;
		}

		tooltipComponents.add(
				Component.literal(
						"Contains: "
								+ fluid.getHoverName().getString()
				).withStyle(ChatFormatting.GREEN)
		);

		tooltipComponents.add(
				Component.literal(
						String.format(
								"%d mB/%d mB",
								fluid.getAmount(),
								16000
						)
				).withStyle(ChatFormatting.BLUE)
		);
	}
}

