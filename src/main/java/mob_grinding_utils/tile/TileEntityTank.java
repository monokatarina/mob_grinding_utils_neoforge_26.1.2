package mob_grinding_utils.tile;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.components.FluidContents;
import mob_grinding_utils.components.MGUComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.Nullable;

public class TileEntityTank extends BlockEntity {

	public static final int DEFAULT_CAPACITY = 32_000;

	public FluidStacksResourceHandler tank;
	public int prevTankAmount;

	public TileEntityTank(
			BlockPos pos,
			BlockState state
	) {
		this(
				ModBlocks.TANK.getTileEntityType(),
				new FluidStacksResourceHandler(
						1,
						DEFAULT_CAPACITY
				),
				pos,
				state
		);
	}

	public TileEntityTank(
			BlockEntityType<? extends TileEntityTank> type,
			BlockPos pos,
			BlockState state
	) {
		this(
				type,
				new FluidStacksResourceHandler(
						1,
						DEFAULT_CAPACITY
				),
				pos,
				state
		);
	}

	public TileEntityTank(
			BlockEntityType<? extends TileEntityTank> type,
			FluidStacksResourceHandler tank,
			BlockPos pos,
			BlockState state
	) {
		super(type, pos, state);
		this.tank = tank;
	}

	public static <T extends BlockEntity> void serverTick(
			Level level,
			BlockPos worldPosition,
			BlockState blockState,
			T blockEntity
	) {
		if (!(blockEntity instanceof TileEntityTank tile)) {
			return;
		}

		int currentAmount =
				tile.tank.getAmountAsInt(0);

		if (tile.prevTankAmount != currentAmount) {
			tile.prevTankAmount = currentAmount;
			tile.setChanged();
			tile.updateBlock();
		}
	}

	public void updateBlock() {
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
				Block.UPDATE_ALL
		);
	}

	public ResourceHandler<FluidResource> getTank() {
		return tank;
	}

	public ResourceHandler<FluidResource> getTank(
			@Nullable Direction direction
	) {
		return tank;
	}

	public int getScaledFluid(int scale) {
		if (scale <= 0) {
			return 0;
		}

		int amount =
				tank.getAmountAsInt(0);

		int capacity =
				tank.getCapacityAsInt(
						0,
						FluidResource.EMPTY
				);

		if (capacity <= 0) {
			return 0;
		}

		return amount * scale / capacity;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		FluidStack storedFluid = FluidUtil.getStack(tank, 0);
		output.storeNullable("tank", FluidStack.CODEC, storedFluid.isEmpty() ? null : storedFluid);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		FluidStack storedFluid = input.read("tank", FluidStack.CODEC).orElse(FluidStack.EMPTY);
		tank.set(
				0,
				FluidResource.of(storedFluid),
				storedFluid.isEmpty() ? 0 : storedFluid.getAmount()
		);
		prevTankAmount = tank.getAmountAsInt(0);
	}

	@Override
	protected void applyImplicitComponents(
			DataComponentGetter componentInput
	) {
		super.applyImplicitComponents(componentInput);

		FluidStack storedFluid =
				componentInput.getOrDefault(
						MGUComponents.FLUID,
						FluidContents.EMPTY
				).get();

		tank.set(
				0,
				FluidResource.of(storedFluid),
				storedFluid.isEmpty()
						? 0
						: storedFluid.getAmount()
		);

		prevTankAmount =
				tank.getAmountAsInt(0);
	}

	@Override
	protected void collectImplicitComponents(
			DataComponentMap.Builder builder
	) {
		super.collectImplicitComponents(builder);

		builder.set(
				MGUComponents.FLUID,
				FluidContents.of(
						FluidUtil.getStack(tank, 0)
				)
		);
	}
}

