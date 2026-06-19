package mob_grinding_utils.inventory.server;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class SlotSolidifierOutput extends ResourceHandlerSlot {

    public SlotSolidifierOutput(
            ResourceHandler<ItemResource> handler,
            IndexModifier<ItemResource> modifier,
            int index,
            int xPosition,
            int yPosition
    ) {
        super(
                handler,
                modifier,
                index,
                xPosition,
                yPosition
        );
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(
            Player player,
            ItemStack stack
    ) {
        super.onTake(player, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
