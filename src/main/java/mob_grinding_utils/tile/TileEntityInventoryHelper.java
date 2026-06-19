package mob_grinding_utils.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public abstract class TileEntityInventoryHelper extends BlockEntity implements WorldlyContainer {

	private NonNullList<ItemStack> inventory;

	public TileEntityInventoryHelper(BlockEntityType<?> tileEntityTypeIn, int invtSize, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		inventory = NonNullList.<ItemStack>withSize(invtSize, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inventory.get(slot);
	}

    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

	@Override
    public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ContainerHelper.removeItem(inventory, index, count);
		if (!itemstack.isEmpty())
			this.setChanged();
		return itemstack;
	}

	@Override
    public void setItem(int index, @Nullable ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize())
            stack.setCount(this.getMaxStackSize());
        this.setChanged();
    }

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventory) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void startOpen(Player playerIn) {
	}

	public void stopOpen(Player playerIn) {
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	public boolean canInsertItem() {
		return false;
	}

	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		NonNullList<ItemStack> restored = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		int index = 0;
		for (ItemStack stack : input.listOrEmpty("items", ItemStack.CODEC)) {
			if (index >= restored.size()) {
				break;
			}
			restored.set(index++, stack);
		}
		inventory = restored;
	}

	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		var list = output.list("items", ItemStack.CODEC);
		for (ItemStack stack : inventory) {
			list.add(stack);
		}
	}
}

