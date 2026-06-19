package mob_grinding_utils.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMonocle extends Item {

	// Construtor sem parâmetros extras
	public ItemMonocle(Properties properties) {
		super(properties);
	}
	public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
		tooltip.add(Component.translatable("tooltip.monocle"));
	}
}
