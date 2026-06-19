package mob_grinding_utils.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemImaginaryInvisibleNotReallyThereSword extends Item {
	public ItemImaginaryInvisibleNotReallyThereSword(Properties properties) {
		super(properties);
	}
	public void appendHoverText(ItemStack stack, @Nonnull net.minecraft.world.item.Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		list.add(Component.literal("Nothing to see here - Move along."));
	}
}




