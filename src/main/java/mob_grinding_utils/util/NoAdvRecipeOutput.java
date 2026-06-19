package mob_grinding_utils.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

public class NoAdvRecipeOutput implements RecipeOutput {

    private final RecipeOutput inner;

    public NoAdvRecipeOutput(RecipeOutput output) {
        this.inner = output;
    }

    @Override
    public Advancement.Builder advancement() {
        return inner.advancement();
    }

    @Override
    public void includeRootAdvancement() {
        inner.includeRootAdvancement();
    }

    @Override
    public void accept(
            ResourceKey<Recipe<?>> id,
            Recipe<?> recipe,
            @Nullable AdvancementHolder advancementHolder,
            ICondition... conditions
    ) {
        inner.accept(id, recipe, null, conditions);
    }
}

