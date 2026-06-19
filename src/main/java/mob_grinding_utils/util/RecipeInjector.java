package mob_grinding_utils.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class RecipeInjector<T extends Recipe<?>> implements RecipeOutput {

    private final RecipeOutput inner;
    private final Function<T, ? extends Recipe<?>> constructor;

    public RecipeInjector(
            RecipeOutput output,
            Function<T, ? extends Recipe<?>> constructor
    ) {
        this.inner = output;
        this.constructor = constructor;
    }

    @Override
    public Advancement.Builder advancement() {
        return inner.advancement();
    }

    @Override
    public void includeRootAdvancement() {
        inner.includeRootAdvancement();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void accept(
            ResourceKey<Recipe<?>> id,
            Recipe<?> recipe,
            @Nullable AdvancementHolder advancement,
            ICondition... conditions
    ) {
        Recipe<?> injected =
                constructor.apply((T) recipe);

        inner.accept(
                id,
                injected,
                advancement,
                conditions
        );
    }
}

