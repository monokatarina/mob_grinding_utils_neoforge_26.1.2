package mob_grinding_utils.inventory.server;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.function.Predicate;

public class RestrictedHandlerSlot extends ResourceHandlerSlot {

    private final Predicate<ItemStack> itemPredicate;
    private final int maxItems;

    public RestrictedHandlerSlot(
            ResourceHandler<ItemResource> handler,
            IndexModifier<ItemResource> modifier,
            int index,
            int xPosition,
            int yPosition,
            Predicate<ItemStack> itemPredicate,
            int max
    ) {
        super(
                handler,
                modifier,
                index,
                xPosition,
                yPosition
        );

        this.itemPredicate = itemPredicate;
        this.maxItems = max;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty()
                && itemPredicate.test(stack)
                && super.mayPlace(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(
                maxItems,
                stack.getMaxStackSize()
        );
    }

    @Override
    public int getMaxStackSize() {
        return maxItems;
    }
}
