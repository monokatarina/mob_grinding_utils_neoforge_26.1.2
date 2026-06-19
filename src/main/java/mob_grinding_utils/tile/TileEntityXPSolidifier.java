package mob_grinding_utils.tile;

import io.netty.buffer.Unpooled;
import mob_grinding_utils.MobGrindingUtils;
import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.ModTags;
import mob_grinding_utils.components.FluidContents;
import mob_grinding_utils.components.MGUComponents;
import mob_grinding_utils.inventory.server.ContainerXPSolidifier;
import mob_grinding_utils.recipe.SolidifyRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class TileEntityXPSolidifier extends BlockEntity
		implements MenuProvider, BEGuiClickable {

	private static final int TANK_CAPACITY = 16_000;

	public final FluidStacksResourceHandler tank =
			new FluidStacksResourceHandler(1, TANK_CAPACITY) {
				@Override
				protected void onContentsChanged(
						int index,
						FluidStack previousContents
				) {
					TileEntityXPSolidifier.this.setChanged();
					TileEntityXPSolidifier.this.updateBlock();
				}
			};

	public final ItemStacksResourceHandler inputSlots =
			new ItemStacksResourceHandler(2) {
				@Override
				protected void onContentsChanged(
						int index,
						ItemStack previousContents
				) {
					TileEntityXPSolidifier.this.currentRecipe = null;
					TileEntityXPSolidifier.this.setChanged();
				}
			};

	public final ItemStacksResourceHandler outputSlot =
			new ItemStacksResourceHandler(1) {
				@Override
				public boolean isValid(
						int index,
						ItemResource resource
				) {
					return false;
				}

				@Override
				protected void onContentsChanged(
						int index,
						ItemStack previousContents
				) {
					TileEntityXPSolidifier.this.setChanged();
				}
			};

	private int previousFluidLevel;
	public int moulding_progress;
	public int MAX_MOULDING_TIME = 100;
	public boolean isOn;
	private @Nullable RecipeHolder<SolidifyRecipe> currentRecipe;

	public boolean active;
	public int animationTicks;
	public int prevAnimationTicks;

	public OutputDirection outputDirection = OutputDirection.NONE;

	public TileEntityXPSolidifier(
			BlockPos pos,
			BlockState state
	) {
		super(
				ModBlocks.XPSOLIDIFIER.getTileEntityType(),
				pos,
				state
		);
	}

	@Override
	public void buttonClicked(int buttonID) {
		switch (buttonID) {
			case 0 -> toggleOutput();
			case 1 -> toggleOnOff();
			default -> {
			}
		}

		updateBlock();
	}

	public enum OutputDirection implements StringRepresentable {
		NONE("none"),
		NORTH("north"),
		EAST("east"),
		SOUTH("south"),
		WEST("west");

		private final String name;

		OutputDirection(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}

		public static OutputDirection fromString(String string) {
			for (OutputDirection direction : values()) {
				if (direction.name.equals(string)) {
					return direction;
				}
			}

			return NONE;
		}
	}

	public ResourceHandler<FluidResource> getTank(
			@Nullable Direction side
	) {
		return tank;
	}

	public ResourceHandler<ItemResource> getOutput(
			@Nullable Direction side
	) {
		return outputSlot;
	}

	public void toggleOutput() {
		outputDirection = switch (outputDirection) {
			case WEST -> OutputDirection.NONE;
			case SOUTH -> OutputDirection.WEST;
			case EAST -> OutputDirection.SOUTH;
			case NORTH -> OutputDirection.EAST;
			case NONE -> OutputDirection.NORTH;
		};

		setChanged();
	}

	public void toggleOnOff() {
		isOn = !isOn;
		setChanged();
	}

	public static <T extends BlockEntity> void tick(
			Level level,
			BlockPos worldPosition,
			BlockState blockState,
			T blockEntity
	) {
		if (!(blockEntity instanceof TileEntityXPSolidifier tile)) {
			return;
		}

		if (level.isClientSide()) {
			tile.clientTick();
			return;
		}

		tile.serverTick(level, worldPosition);
	}

	private void clientTick() {
		if (!isOn || !active) {
			prevAnimationTicks = 0;
			animationTicks = 0;
			return;
		}

		prevAnimationTicks = animationTicks;
		animationTicks += 1 + getModifierAmount();

		if (animationTicks >= MAX_MOULDING_TIME) {
			animationTicks -= MAX_MOULDING_TIME;
			prevAnimationTicks -= MAX_MOULDING_TIME;
		}
	}

	private void serverTick(
			Level level,
			BlockPos worldPosition
	) {
		if (!isOn) {
			stopOperation();
			checkFluidChanged();
			return;
		}

		updateCurrentRecipe();

		if (hasFluid() && canOperate()) {
			setActive(true);
			setProgress(
					getProgress() + 1 + getModifierAmount()
			);

			if (getProgress() >= MAX_MOULDING_TIME) {
				completeRecipe();
			}
		} else {
			stopOperation();
		}

		tryOutputItem(level, worldPosition);
		checkFluidChanged();
	}

	private void updateCurrentRecipe() {
		ItemStack mould = getInputStack(0);

		if (currentRecipe != null
				&& !currentRecipe.value().matches(mould)) {
			currentRecipe = null;
		}

		if (currentRecipe == null && !mould.isEmpty()) {
			currentRecipe = getRecipeForMould(mould);
		}
	}

	private void completeRecipe() {
		RecipeHolder<SolidifyRecipe> recipe = currentRecipe;

		if (recipe == null) {
			stopOperation();
			return;
		}

		ItemStack result = recipe.value().result().create();

		if (result.isEmpty()) {
			stopOperation();
			return;
		}

		FluidResource fluidResource = tank.getResource(0);
		int fluidAmount = recipe.value().fluidAmount();
		ItemResource resultResource = ItemResource.of(result);

		if (fluidResource.isEmpty() || resultResource.isEmpty()) {
			stopOperation();
			return;
		}

		try (Transaction transaction = Transaction.openRoot()) {
			int drained = tank.extract(
					0,
					fluidResource,
					fluidAmount,
					transaction
			);

			int inserted = outputSlot.insert(
					0,
					resultResource,
					result.getCount(),
					transaction
			);

			if (drained == fluidAmount
					&& inserted == result.getCount()) {
				transaction.commit();
				setProgress(0);
				setActive(false);
				setChanged();
			}
		}
	}

	private void tryOutputItem(
			Level level,
			BlockPos worldPosition
	) {
		Direction outputFacing = getOutputFacing();

		if (outputFacing == null || isOutputEmpty()) {
			return;
		}

		BlockPos targetPos =
				worldPosition.relative(outputFacing);

		ResourceHandler<ItemResource> destination =
				level.getCapability(
						Capabilities.Item.BLOCK,
						targetPos,
						outputFacing.getOpposite()
				);

		if (destination == null) {
			return;
		}

		int moved = ResourceHandlerUtil.moveStacking(
				outputSlot,
				destination,
				resource -> true,
				1,
				null
		);

		if (moved > 0) {
			setChanged();
		}
	}

	private void stopOperation() {
		if (getProgress() > 0 || active) {
			setProgress(0);
			setActive(false);
		}
	}

	private void checkFluidChanged() {
		int currentFluidLevel = tank.getAmountAsInt(0);

		if (previousFluidLevel != currentFluidLevel) {
			previousFluidLevel = currentFluidLevel;
			updateBlock();
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private @Nullable Direction getOutputFacing() {
		return switch (outputDirection) {
			case WEST -> Direction.WEST;
			case SOUTH -> Direction.SOUTH;
			case EAST -> Direction.EAST;
			case NORTH -> Direction.NORTH;
			case NONE -> null;
		};
	}

	private ItemStack getInputStack(int slot) {
		return ItemUtil.getStack(inputSlots, slot);
	}

	private ItemStack getOutputStack() {
		return ItemUtil.getStack(outputSlot, 0);
	}
	public ItemStack getCachedOutPutRenderStack() {
		if (hasMould()
				&& getInputStack(0).getItem()
				== ModItems.SOLID_XP_MOULD_BABY.get()) {
			return new ItemStack(
					ModItems.SOLID_XP_BABY.get(),
					1
			);
		}

		return ItemStack.EMPTY;
	}
	public int getProgressScaled(int count) {
		return getProgress() * count / MAX_MOULDING_TIME;
	}

	private boolean hasFluid() {
		RecipeHolder<SolidifyRecipe> recipe = currentRecipe;

		if (recipe == null) {
			return false;
		}

		FluidStack fluidStack = FluidUtil.getStack(tank, 0);

		return !fluidStack.isEmpty()
				&& fluidStack.getAmount()
				>= recipe.value().fluidAmount()
				&& fluidStack.getFluid()
				.is(ModTags.Fluids.EXPERIENCE);
	}

	private boolean canOperate() {
		return hasMould() && isOutputEmpty();
	}

	private boolean hasMould() {
		return currentRecipe != null
				&& currentRecipe.value()
				.matches(getInputStack(0));
	}

	public static @Nullable RecipeHolder<SolidifyRecipe>
	getRecipeForMould(ItemStack stack) {
		return MobGrindingUtils.SOLIDIFIER_RECIPES
				.stream()
				.filter(recipe ->
						recipe.value().matches(stack)
				)
				.findFirst()
				.orElse(null);
	}

	private boolean isOutputEmpty() {
		return getOutputStack().isEmpty();
	}

	private boolean hasUpgrade() {
		ItemStack stack = getInputStack(1);

		return !stack.isEmpty()
				&& stack.getItem()
				== ModItems.XP_SOLIDIFIER_UPGRADE.get();
	}

	public int getModifierAmount() {
		return hasUpgrade()
				? inputSlots.getAmountAsInt(1)
				: 0;
	}

	private void setProgress(int counter) {
		moulding_progress = counter;
	}

	public int getProgress() {
		return moulding_progress;
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

	public void onContentsChanged() {
		setChanged();
		updateBlock();
	}

	public int getScaledFluid(int scale) {
		return tank.getAmountAsInt(0)
				* scale
				/ TANK_CAPACITY;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(
				"block.mob_grinding_utils.xpsolidifier"
		);
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(
			int windowID,
			Inventory playerInventory,
			Player player
	) {
		return new ContainerXPSolidifier(
				windowID,
				playerInventory,
				new FriendlyByteBuf(Unpooled.buffer())
						.writeBlockPos(worldPosition)
		);
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

		FluidResource resource =
				FluidResource.of(storedFluid);

		tank.set(
				0,
				resource,
				storedFluid.isEmpty()
						? 0
						: storedFluid.getAmount()
		);
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
		previousFluidLevel = tank.getAmountAsInt(0);
	}
}

