package mob_grinding_utils.blocks;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.tile.TileEntityMGUSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jspecify.annotations.Nullable;

public class BlockEntitySpawner extends Block implements EntityBlock {

	public static final EnumProperty<Direction> FACING =
			HorizontalDirectionalBlock.FACING;

	public static final BooleanProperty POWERED =
			BlockStateProperties.POWERED;

	public BlockEntitySpawner(Block.Properties properties) {
		super(properties);

		registerDefaultState(
				stateDefinition.any()
						.setValue(FACING, Direction.NORTH)
						.setValue(POWERED, false)
		);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityMGUSpawner(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level,
			BlockState state,
			BlockEntityType<T> blockEntityType
	) {
		return level.isClientSide()
				? TileEntityMGUSpawner::clientTick
				: TileEntityMGUSpawner::serverTick;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction =
				context.getHorizontalDirection().getOpposite();

		return defaultBlockState()
				.setValue(FACING, direction)
				.setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(
			StateDefinition.Builder<Block, BlockState> builder
	) {
		builder.add(FACING, POWERED);
	}

	@Override
	public InteractionResult useWithoutItem(
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			BlockHitResult hitResult
	) {
		if (!level.isClientSide()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof TileEntityMGUSpawner tile) {
				player.openMenu(tile, pos);
			}
		}

		return InteractionResult.SUCCESS;
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

			if (blockEntity instanceof TileEntityMGUSpawner tile) {
				dropHandlerContents(
						level,
						pos,
						tile.inputSlots
				);

				dropHandlerContents(
						level,
						pos,
						tile.fuelSlot
				);

				level.removeBlockEntity(pos);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	private static void dropHandlerContents(
			Level level,
			BlockPos pos,
			net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler handler
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
	public void neighborChanged(
			BlockState state,
			Level level,
			BlockPos pos,
			Block block,
			@Nullable Orientation orientation,
			boolean isMoving
	) {
		if (level.isClientSide()) {
			return;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);
		TileEntityMGUSpawner tile =
				blockEntity instanceof TileEntityMGUSpawner spawner
						? spawner
						: null;

		boolean powered = state.getValue(POWERED);
		boolean hasSignal = level.hasNeighborSignal(pos);

		if (powered == hasSignal) {
			return;
		}

		if (powered) {
			level.scheduleTick(pos, this, 4);
			return;
		}

		level.setBlock(
				pos,
				state.setValue(POWERED, true),
				2
		);

		if (tile != null) {
			tile.isOn = false;
			tile.setChanged();
		}
	}

	@Override
	public void tick(
			BlockState state,
			ServerLevel level,
			BlockPos pos,
			RandomSource random
	) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		TileEntityMGUSpawner tile =
				blockEntity instanceof TileEntityMGUSpawner spawner
						? spawner
						: null;

		if (state.getValue(POWERED)
				&& !level.hasNeighborSignal(pos)) {

			level.setBlock(
					pos,
					state.setValue(POWERED, false),
					2
			);

			if (tile != null) {
				tile.isOn = true;
				tile.setChanged();
			}
		}
	}

	@Override
	public boolean getWeakChanges(
			BlockState state,
			LevelReader level,
			BlockPos pos
	) {
		return state.is(ModBlocks.ENTITY_SPAWNER.getBlock());
	}
}

