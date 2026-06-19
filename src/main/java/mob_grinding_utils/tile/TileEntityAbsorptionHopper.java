package mob_grinding_utils.tile;

import io.netty.buffer.Unpooled;
import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.inventory.server.ContainerAbsorptionHopper;
import mob_grinding_utils.inventory.server.InventoryWrapperAH;
import mob_grinding_utils.util.CapHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TileEntityAbsorptionHopper extends TileEntityInventoryHelper implements MenuProvider, BEGuiClickable {

	private final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, 1000 * 16) {
		@Override
		protected void onContentsChanged(int index, net.neoforged.neoforge.fluids.FluidStack previousContents) {
			TileEntityAbsorptionHopper.this.setChanged();
			TileEntityAbsorptionHopper.this.updateBlock();
		}
	};
	private final ResourceHandler<ItemResource> itemHandler;

	private static final int[] SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
	public long prevTankAmount;

	public TileEntityAbsorptionHopper(BlockPos pos, BlockState state) {
		super(ModBlocks.ABSORPTION_HOPPER.getTileEntityType(), 17, pos, state);
		itemHandler = createUnSidedHandler();
	}

	@Override
	public void buttonClicked(int buttonID) {
		switch (buttonID) {
			case 0,1,2,3,4,5 -> toggleMode(Direction.values()[buttonID]);
			case 6 -> toggleRenderBox();
			case 7,8,9,10,11,12 -> toggleOffset(buttonID);
		}
		updateBlock();
	}

	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		return itemHandler;
	}

	public ResourceHandler<FluidResource> getTank(final Direction side) {
		return tank;
	}

	public long getTankAmount() {
		return tank.getAmountAsLong(0);
	}

	public long getTankCapacity() {
		return tank.getCapacityAsLong(0, tank.getResource(0));
	}

	public FluidResource getTankFluid() {
		return tank.getResource(0);
	}

	public enum EnumStatus implements StringRepresentable {
		STATUS_NONE("none"),
		STATUS_OUTPUT_ITEM("item"),
		STATUS_OUTPUT_FLUID("fluid");

		private final String name;

		EnumStatus(String name) {
			this.name = name;
		}

		@Nonnull
		@Override
		public String getSerializedName() {
			return name;
		}

		public static EnumStatus fromString(String name) {
			for (EnumStatus value : values()) {
				if (value.name.equals(name)) {
					return value;
				}
			}
			return STATUS_NONE;
		}
	}

	public EnumStatus[] status = new EnumStatus[] {
			EnumStatus.STATUS_NONE, EnumStatus.STATUS_NONE, EnumStatus.STATUS_NONE,
			EnumStatus.STATUS_NONE, EnumStatus.STATUS_NONE, EnumStatus.STATUS_NONE
	};

	public boolean showRenderBox;
	public int offsetX, offsetY, offsetZ;

	public EnumStatus getSideStatus(Direction side) {
		return status[side.ordinal()];
	}

	public void toggleMode(Direction side) {
		switch (status[side.ordinal()]) {
			case STATUS_NONE:
				status[side.ordinal()] = EnumStatus.STATUS_OUTPUT_ITEM;
				break;
			case STATUS_OUTPUT_ITEM:
				status[side.ordinal()] = EnumStatus.STATUS_OUTPUT_FLUID;
				break;
			case STATUS_OUTPUT_FLUID:
				status[side.ordinal()] = EnumStatus.STATUS_NONE;
				break;
		}
		setChanged();
	}

	public void toggleRenderBox() {
		showRenderBox = !showRenderBox;
		setChanged();
	}

	public void toggleOffset(int direction) {
		switch (direction) {
			case 7:
				if (getoffsetY() >= -3 - getModifierAmount())
					offsetY = getoffsetY() - 1;
				break;
			case 8:
				if (getoffsetY() <= 3 + getModifierAmount())
					offsetY = getoffsetY() + 1;
				break;
			case 9:
				if (getoffsetZ() >= -3 - getModifierAmount())
					offsetZ = getoffsetZ() - 1;
				break;
			case 10:
				if (getoffsetZ() <= 3 + getModifierAmount())
					offsetZ = getoffsetZ() + 1;
				break;
			case 11:
				if (getoffsetX() >= -3 - getModifierAmount())
					offsetX = getoffsetX() - 1;
				break;
			case 12:
				if (getoffsetX() <= 3 + getModifierAmount())
					offsetX = getoffsetX() + 1;
				break;
		}
		setChanged();
	}

	public void updateBlock() {
		Level level = getLevel();
		if (level != null) {
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
		}
	}

	public static <T extends BlockEntity> void serverTick(Level level, BlockPos worldPosition, BlockState blockState, T t) {
		if (t instanceof TileEntityAbsorptionHopper tile) {
			tile.prevTankAmount = tile.getTankAmount();
			final TileEntityAbsorptionHopper tileRef = tile; // Para usar em lambdas

			for (Direction facing : Direction.values()) {
				// --- LOGICA DE ITENS ---
				if (tileRef.status[facing.ordinal()] == EnumStatus.STATUS_OUTPUT_ITEM) {
					BlockEntity otherTile = level.getBlockEntity(worldPosition.relative(facing));
					Optional<ResourceHandler<ItemResource>> handlerOptional = CapHelper.getItemHandler(level, worldPosition.relative(facing), facing.getOpposite());

					if (otherTile != null && handlerOptional.isPresent()) {
						handlerOptional.ifPresent((handler) -> {
							if (level.getGameTime() % 8 == 0) {
								for (int i = 0; i < tileRef.getContainerSize(); ++i) {
									if (!tileRef.getItem(i).isEmpty() && i != 0) {
										ItemStack stack = tileRef.getItem(i).copy();
										stack.setCount(1);
										ItemResource resource = ItemResource.of(stack);

										try (Transaction transaction = Transaction.openRoot()) {
											int inserted = handler.insert(resource, 1, transaction);
											if (inserted > 0) {
												transaction.commit();
												tileRef.removeItem(i, 1);
												tileRef.setChanged();
											}
										}
									}
								}
							}
						});
					} else if (otherTile instanceof Container iinventory) {
						if (tileRef.isInventoryFull(iinventory, facing))
							break;
						else if (level.getGameTime() % 8 == 0) {
							for (int i = 0; i < tileRef.getContainerSize(); ++i) {
								if (!tileRef.getItem(i).isEmpty() && i != 0) {
									ItemStack stack = tileRef.getItem(i).copy();
									ItemStack stack1 = putStackInInventoryAllSlots(iinventory, tileRef.removeItem(i, 1), facing.getOpposite());
									if (stack1.isEmpty() || stack1.getCount() == 0)
										iinventory.setChanged();
									else
										tileRef.setItem(i, stack);
								}
							}
						}
					}
				}

				// --- LOGICA DE FLUIDOS ---
				if (tileRef.status[facing.ordinal()] == EnumStatus.STATUS_OUTPUT_FLUID) {
					Optional<ResourceHandler<FluidResource>> handlerOptional = CapHelper.getFluidHandler(level, worldPosition.relative(facing), facing.getOpposite());
					handlerOptional.ifPresent((receptacle) -> {
						FluidResource fluidResource = tileRef.getTankFluid();
						if (!fluidResource.isEmpty()) {
							try (Transaction transaction = Transaction.openRoot()) {
								long capacity = receptacle.getCapacityAsLong(0, fluidResource);
								long current = receptacle.getAmountAsLong(0);

								if (current <= capacity - 100) {
									int inserted = receptacle.insert(0, fluidResource, 100, transaction);
									if (inserted > 0) {
										int drained = tileRef.tank.extract(0, fluidResource, inserted, transaction);
										if (drained > 0) {
											transaction.commit();
											tileRef.setChanged();
										}
									}
								}
							}
						}
					});
				}
			}

			if (level.getGameTime() % 3 == 0 && !level.hasNeighborSignal(worldPosition)) {
				if (!tileRef.isInventoryFull(tileRef, null))
					tileRef.captureDroppedItems();
				// CORRIGIDO: Comparação direta de fluido
				if (tileRef.getTankFluid().isEmpty() || tileRef.getTankFluid().getFluid() == ModBlocks.FLUID_XP.get())
					tileRef.captureDroppedXP();
			}

			if (tileRef.prevTankAmount != tileRef.getTankAmount())
				tileRef.updateBlock();
		}
	}

	@Override
	@Nullable
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(getItems(), index, count);
	}

	public boolean captureDroppedItems() {
		for (ItemEntity entityitem : getCaptureItems()) {
			if (putDropInInventoryAllSlots(this, entityitem))
				return true;
		}
		return false;
	}

	public List<ItemEntity> getCaptureItems() {
		Level level = getLevel();
		if (level == null) return List.of();
		return level.<ItemEntity>getEntitiesOfClass(ItemEntity.class, getAABBWithModifiers(), EntitySelector.ENTITY_STILL_ALIVE);
	}

	public boolean captureDroppedXP() {
		for (ExperienceOrb entity : getCaptureXP()) {
			int xpAmount = entity.getValue();
			FluidResource xpFluid = FluidResource.of(ModBlocks.FLUID_XP.get());

			try (Transaction transaction = Transaction.openRoot()) {
				long capacity = getTankCapacity();
				long current = getTankAmount();

				if (current < capacity - (xpAmount * 20L)) {
					int inserted = tank.insert(0, xpFluid, xpAmount * 20, transaction);
					if (inserted > 0) {
						transaction.commit();
						entity.setValue(0);
						entity.remove(Entity.RemovalReason.DISCARDED);
					}
				}
			}
			return true;
		}
		return false;
	}

	public List<ExperienceOrb> getCaptureXP() {
		Level level = getLevel();
		if (level == null) return List.of();
		return level.<ExperienceOrb>getEntitiesOfClass(ExperienceOrb.class, getAABBWithModifiers(), EntitySelector.ENTITY_STILL_ALIVE);
	}

	public AABB getAABBWithModifiers() {
		double x = getBlockPos().getX() + 0.5D;
		double y = getBlockPos().getY() + 0.5D;
		double z = getBlockPos().getZ() + 0.5D;
		return new AABB(
				x - 3.5D - getModifierAmount(),
				y - 3.5D - getModifierAmount(),
				z - 3.5D - getModifierAmount(),
				x + 3.5D + getModifierAmount(),
				y + 3.5D + getModifierAmount(),
				z + 3.5D + getModifierAmount()
		).move(getoffsetX(), getoffsetY(), getoffsetZ());
	}
	public AABB getAABBForRender() {
		return new AABB(
				-3D - getModifierAmount(),
				-3D - getModifierAmount(),
				-3D - getModifierAmount(),
				4D + getModifierAmount(),
				4D + getModifierAmount(),
				4D + getModifierAmount()
		).move(getoffsetX(), getoffsetY(), getoffsetZ());
	}

	public int getoffsetX() {
		return Math.max(-4 - getModifierAmount(), Math.min(offsetX, 4 + getModifierAmount()));
	}

	public int getoffsetY() {
		return Math.max(-4 - getModifierAmount(), Math.min(offsetY, 4 + getModifierAmount()));
	}

	public int getoffsetZ() {
		return Math.max(-4 - getModifierAmount(), Math.min(offsetZ, 4 + getModifierAmount()));
	}

	private boolean hasUpgrade() {
		return !getItems().get(0).isEmpty() && getItems().get(0).getItem() == ModItems.ABSORPTION_UPGRADE.get();
	}

	public int getModifierAmount() {
		return hasUpgrade() ? getItems().get(0).getCount() : 0;
	}

	@Nonnull
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(getItems(), index);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot != 0;
	}

	@Nonnull
	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot != 0;
	}

	private boolean isInventoryFull(Container inventoryIn, Direction side) {
		if (inventoryIn instanceof WorldlyContainer isidedinventory) {
			int[] aint = isidedinventory.getSlotsForFace(side);
			for (int k : aint) {
				ItemStack itemstack1 = isidedinventory.getItem(k);
				if (itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize())
					return false;
			}
		} else {
			int i = inventoryIn.getContainerSize();
			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inventoryIn.getItem(j);
				if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize())
					return false;
			}
		}
		return true;
	}

	public static ItemStack putStackInInventoryAllSlots(Container inventory, ItemStack stack, @Nullable Direction facing) {
		if (inventory instanceof TileEntityAbsorptionHopper) {
			int i = inventory.getContainerSize();
			for (int j = 1; j < i && !stack.isEmpty(); ++j)
				stack = insertStack(inventory, stack, j, facing);
		} else if (inventory instanceof WorldlyContainer isidedinventory && facing != null &&
				inventory.canPlaceItem(0, stack.copy())) {
			int[] aint = isidedinventory.getSlotsForFace(facing);
			for (int k = 0; k < aint.length && !stack.isEmpty(); ++k)
				stack = insertStack(inventory, stack, aint[k], facing);
		} else {
			int i = inventory.getContainerSize();
			for (int j = 0; j < i && !stack.isEmpty(); ++j)
				stack = insertStack(inventory, stack, j, facing);
		}
		return stack;
	}

	public static boolean putDropInInventoryAllSlots(Container inventoryIn, ItemEntity itemIn) {
		boolean flag = false;

		if (itemIn == null || inventoryIn instanceof TileEntityAbsorptionHopper && inventoryIn.canPlaceItem(0, itemIn.getItem().copy())) {
			return false;
		} else {
			ItemStack itemstack = itemIn.getItem().copy();
			ItemStack itemstack1 = putStackInInventoryAllSlots(inventoryIn, itemstack, null);

			if (!itemstack1.isEmpty()) {
				itemIn.setItem(itemstack1);
			} else {
				flag = true;
				itemIn.remove(Entity.RemovalReason.DISCARDED);
			}
			return flag;
		}
	}

	private static boolean canInsertItemInSlot(Container inventoryIn, ItemStack stack, int index, Direction side) {
		return inventoryIn.canPlaceItem(index, stack) && (!(inventoryIn instanceof WorldlyContainer) || ((WorldlyContainer) inventoryIn).canPlaceItemThroughFace(index, stack, side));
	}

	private static ItemStack insertStack(Container inventory, ItemStack stack, int index, Direction side) {
		ItemStack itemstack = inventory.getItem(index);
		if (canInsertItemInSlot(inventory, stack, index, side)) {
			if (itemstack.isEmpty()) {
				inventory.setItem(index, stack);
				stack = ItemStack.EMPTY;
			}
			else if (canCombine(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.shrink(j);
				itemstack.grow(j);
			}
		}
		return stack;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() &&
				stack1.getDamageValue() == stack2.getDamageValue() &&
				stack1.getCount() <= stack1.getMaxStackSize() &&
				ItemStack.isSameItemSameComponents(stack1, stack2);
	}

	protected ResourceHandler<ItemResource> createUnSidedHandler() {
		return new InventoryWrapperAH(this);
	}

	public int getScaledFluid(int scale) {
		long amount = getTankAmount();
		long capacity = getTankCapacity();
		return amount > 0 ? (int) ((float) amount / (float) capacity * scale) : 0;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		FluidStack storedFluid = FluidUtil.getStack(tank, 0);
		output.storeNullable("tank", FluidStack.CODEC, storedFluid.isEmpty() ? null : storedFluid);
		output.putBoolean("showRenderBox", showRenderBox);
		output.putInt("offsetX", offsetX);
		output.putInt("offsetY", offsetY);
		output.putInt("offsetZ", offsetZ);
		for (Direction direction : Direction.values()) {
			output.putString("status_" + direction.getSerializedName(), status[direction.ordinal()].getSerializedName());
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		FluidStack storedFluid = input.read("tank", FluidStack.CODEC).orElse(FluidStack.EMPTY);
		tank.set(0, FluidResource.of(storedFluid), storedFluid.isEmpty() ? 0 : storedFluid.getAmount());
		showRenderBox = input.getBooleanOr("showRenderBox", false);
		offsetX = input.getIntOr("offsetX", 0);
		offsetY = input.getIntOr("offsetY", 0);
		offsetZ = input.getIntOr("offsetZ", 0);
		for (Direction direction : Direction.values()) {
			status[direction.ordinal()] = EnumStatus.fromString(input.getStringOr("status_" + direction.getSerializedName(), EnumStatus.STATUS_NONE.getSerializedName()));
		}
		prevTankAmount = getTankAmount();
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player player) {
		return new ContainerAbsorptionHopper(windowID, playerInventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(worldPosition));
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return Component.translatable("block.mob_grinding_utils.absorption_hopper");
	}
}

