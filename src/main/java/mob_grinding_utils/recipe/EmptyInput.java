package mob_grinding_utils.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public final class EmptyInput implements RecipeInput {

    public static final EmptyInput INSTANCE = new EmptyInput();

    private EmptyInput() {
    }

    @Override
    public ItemStack getItem(int index) {
        throw new IndexOutOfBoundsException(
                "EmptyInput has no slots"
        );
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}

