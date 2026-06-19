package mob_grinding_utils.inventory.server;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class SlotRestrictSizeOnly extends ResourceHandlerSlot {

    private final int stackLimit;

    public SlotRestrictSizeOnly(
            ResourceHandler<ItemResource> handler,
            IndexModifier<ItemResource> modifier,
            int index,
            int xPosition,
            int yPosition,
            int max
    ) {
        super(
                handler,
                modifier,
                index,
                xPosition,
                yPosition
        );

        this.stackLimit = max;
    }

    @Override
    public int getMaxStackSize() {
        return stackLimit;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(
                stackLimit,
                stack.getMaxStackSize()
        );
    }
}
