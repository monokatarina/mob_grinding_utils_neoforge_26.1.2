package mob_grinding_utils.inventory.server;

import mob_grinding_utils.ModContainers;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.tile.TileEntityMGUSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import javax.annotation.Nonnull;

public class ContainerMGUSpawner extends AbstractContainerMenu {

    private static final int MACHINE_SLOT_COUNT = 5;

    public final TileEntityMGUSpawner tile;

    public ContainerMGUSpawner(
            int windowId,
            Inventory playerInventory,
            FriendlyByteBuf extra
    ) {
        super(
                ModContainers.ENTITY_SPAWNER.get(),
                windowId
        );

        BlockPos tilePos = extra.readBlockPos();

        BlockEntity blockEntity =
                playerInventory.player
                        .level()
                        .getBlockEntity(tilePos);

        if (!(blockEntity instanceof TileEntityMGUSpawner spawner)) {
            throw new IllegalStateException(
                    "Expected TileEntityMGUSpawner at "
                            + tilePos
                            + ", but found "
                            + blockEntity
            );
        }

        this.tile = spawner;

        addSlots(playerInventory);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    private void addSlots(Inventory playerInventory) {
        int originX = 7;
        int originY = 143;

        // Spawn egg
        addSlot(new SlotRestrictSizeOnly(
                tile.inputSlots,
                tile.inputSlots::set,
                0,
                44,
                22,
                1
        ));

        // Fuel
        addSlot(new RestrictedHandlerSlot(
                tile.fuelSlot,
                tile.fuelSlot::set,
                0,
                44,
                76,
                stack -> stack.getItem() == ModItems.SOLID_XP_BABY.get(),
                64
        ));

        // Width upgrade
        addSlot(new RestrictedHandlerSlot(
                tile.inputSlots,
                tile.inputSlots::set,
                1,
                8,
                112,
                stack -> stack.getItem() == ModItems.SPAWNER_UPGRADE_WIDTH.get(),
                5
        ));

        // Height upgrade
        addSlot(new RestrictedHandlerSlot(
                tile.inputSlots,
                tile.inputSlots::set,
                2,
                44,
                112,
                stack -> stack.getItem() == ModItems.SPAWNER_UPGRADE_HEIGHT.get(),
                5
        ));

        // Speed upgrade
        addSlot(new RestrictedHandlerSlot(
                tile.inputSlots,
                tile.inputSlots::set,
                3,
                80,
                112,
                stack -> stack.getItem() == ModItems.XP_SOLIDIFIER_UPGRADE.get(),
                5
        ));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int x = originX + column * 18;
                int y = originY + row * 18;
                int slotIndex = column + row * 9 + 9;

                addSlot(new Slot(
                        playerInventory,
                        slotIndex,
                        x + 1,
                        y + 1
                ));
            }
        }

        // Hotbar
        for (int column = 0; column < 9; column++) {
            int x = originX + column * 18;
            int y = originY + 58;

            addSlot(new Slot(
                    playerInventory,
                    column,
                    x + 1,
                    y + 1
            ));
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

        if (index >= MACHINE_SLOT_COUNT) {
            if (!moveFromPlayerInventory(sourceStack)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(
                    sourceStack,
                    MACHINE_SLOT_COUNT,
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

    private boolean moveFromPlayerInventory(
            ItemStack stack
    ) {
        if (stack.getItem() instanceof SpawnEggItem) {
            return moveItemStackTo(
                    stack,
                    0,
                    1,
                    false
            );
        }

        if (stack.getItem()
                == ModItems.SOLID_XP_BABY.get()) {
            return moveItemStackTo(
                    stack,
                    1,
                    2,
                    false
            );
        }

        if (stack.getItem()
                == ModItems.SPAWNER_UPGRADE_WIDTH.get()) {
            return moveItemStackTo(
                    stack,
                    2,
                    3,
                    false
            );
        }

        if (stack.getItem()
                == ModItems.SPAWNER_UPGRADE_HEIGHT.get()) {
            return moveItemStackTo(
                    stack,
                    3,
                    4,
                    false
            );
        }

        if (stack.getItem()
                == ModItems.XP_SOLIDIFIER_UPGRADE.get()) {
            return moveItemStackTo(
                    stack,
                    4,
                    5,
                    false
            );
        }

        return false;
    }
}

