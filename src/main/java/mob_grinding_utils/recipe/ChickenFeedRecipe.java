package mob_grinding_utils.recipe;

import com.mojang.serialization.MapCodec;
import mob_grinding_utils.MobGrindingUtils;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.components.MGUComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.CraftingBookCategory;

import java.util.List;

public class ChickenFeedRecipe implements CraftingRecipe {

    public static final String NAME = "chicken_feed";

    private final ShapelessRecipe baseRecipe;

    public ChickenFeedRecipe(ShapelessRecipe baseRecipe) {
        this.baseRecipe = baseRecipe;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return baseRecipe.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = baseRecipe.assemble(input);

        ItemStack usedSwab = ItemStack.EMPTY;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);

            if (!stack.isEmpty()
                    && stack.getItem() == ModItems.MOB_SWAB_USED.get()) {
                usedSwab = stack;
                break;
            }
        }

        if (usedSwab.isEmpty()
                || !usedSwab.has(MGUComponents.MOB_DNA)) {
            return ItemStack.EMPTY;
        }

        result.set(
                MGUComponents.MOB_DNA,
                usedSwab.get(MGUComponents.MOB_DNA)
        );

        return result;
    }

    @Override
    public boolean isSpecial() {
        return baseRecipe.isSpecial();
    }

    @Override
    public boolean showNotification() {
        return baseRecipe.showNotification();
    }

    @Override
    public String group() {
        return baseRecipe.group();
    }

    @Override
    public RecipeSerializer<ChickenFeedRecipe> getSerializer() {
        return MobGrindingUtils.CHICKEN_FEED.get();
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public PlacementInfo placementInfo() {
        return baseRecipe.placementInfo();
    }

    @Override
    public List<RecipeDisplay> display() {
        return baseRecipe.display();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return baseRecipe.recipeBookCategory();
    }

    @Override
    public CraftingBookCategory category() {
        return baseRecipe.category();
    }

    public ShapelessRecipe baseRecipe() {
        return baseRecipe;
    }

    public static final class Serializer {

        public static final MapCodec<ChickenFeedRecipe> CODEC =
                ShapelessRecipe.MAP_CODEC.xmap(
                        ChickenFeedRecipe::new,
                        ChickenFeedRecipe::baseRecipe
                );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                ChickenFeedRecipe
                > STREAM_CODEC = ShapelessRecipe.STREAM_CODEC.map(
                ChickenFeedRecipe::new,
                ChickenFeedRecipe::baseRecipe
        );

        private Serializer() {
        }
    }
}

