package mob_grinding_utils.inventory.server;

import mob_grinding_utils.MobGrindingUtils;
import mob_grinding_utils.ModContainers;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.recipe.SolidifyRecipe;
import mob_grinding_utils.tile.TileEntityXPSolidifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.function.Predicate;


public class ContainerXPSolidifier extends AbstractContainerMenu {
    public TileEntityXPSolidifier tile;

    public ContainerXPSolidifier(final int windowId, final Inventory playerInventory, FriendlyByteBuf extra) {
        super(ModContainers.SOLIDIFIER.get(), windowId);
        BlockPos tilePos = extra.readBlockPos();
        BlockEntity blockEntity =
                playerInventory.player.level()
                        .getBlockEntity(tilePos);

        if (!(blockEntity instanceof TileEntityXPSolidifier solidifier)) {
            throw new IllegalStateException(
                    "Expected TileEntityXPSolidifier at "
                            + tilePos
                            + ", but found "
                            + blockEntity
            );
        }

        this.tile = solidifier;


        addPlayerSlots(playerInventory);
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }

    private void addPlayerSlots(Inventory playerInventory) {
        int originX = 7;
        int originY = 103;
        Predicate<ItemStack> mouldPredicate = stack -> {
            for (RecipeHolder<SolidifyRecipe> recipe : MobGrindingUtils.SOLIDIFIER_RECIPES) {
                if(recipe.value().matches(stack))
                    return true;
            }
            return false;
        };
// Molde
        this.addSlot(new RestrictedHandlerSlot(
                tile.inputSlots,
                tile.inputSlots::set,
                0,
                62,
                36,
                mouldPredicate,
                1
        ));

// Upgrade
        this.addSlot(new RestrictedHandlerSlot(
                tile.inputSlots,
                tile.inputSlots::set,
                1,
                26,
                72,
                stack -> stack.getItem() == ModItems.XP_SOLIDIFIER_UPGRADE.get(),
                9
        ));
        //Output
        this.addSlot(new SlotSolidifierOutput(
                tile.outputSlot,
                tile.outputSlot::set,
                0,
                130,
                36
        ));

        //Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = originX + col * 18;
                int y = originY + row * 18;
                int index = (col + row * 9) + 9;
                this.addSlot(new Slot(playerInventory, index, x+1, y+1));
            }
        }

        //Hotbar
        for (int col = 0; col < 9; col++) {
            int x = originX + col * 18;
            int y = originY + 58;
            this.addSlot(new Slot(playerInventory, col, x+1, y+1));
        }

    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(
            @Nonnull Player player,
            int index
    ) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();
        ItemStack originalStack = sourceStack.copy();

        // Slots da máquina:
        // 0 = molde
        // 1 = upgrade
        // 2 = saída
        if (index >= 3) {
            boolean moved;

            if (sourceStack.getItem()
                    == ModItems.XP_SOLIDIFIER_UPGRADE.get()) {
                moved = moveItemStackTo(
                        sourceStack,
                        1,
                        2,
                        false
                );
            } else {
                moved = moveItemStackTo(
                        sourceStack,
                        0,
                        1,
                        false
                );
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(
                    sourceStack,
                    3,
                    slots.size(),
                    true
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (sourceStack.getCount()
                == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, sourceStack);

        return originalStack;
    }
}


