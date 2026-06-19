package mob_grinding_utils;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ServerResourceReloader implements ResourceManagerReloadListener {

    private final ReloadableServerResources dataPackRegistries;

    public ServerResourceReloader(
            ReloadableServerResources dataPackRegistries
    ) {
        this.dataPackRegistries = dataPackRegistries;
    }

    @Override
    public void onResourceManagerReload(
            ResourceManager resourceManager
    ) {
        RecipeManager recipeManager =
                this.dataPackRegistries.getRecipeManager();

        MobGrindingUtils.SOLIDIFIER_RECIPES.clear();
        MobGrindingUtils.SOLIDIFIER_RECIPES.addAll(
                recipeManager
                        .recipeMap()
                        .byType(
                                MobGrindingUtils
                                        .SOLIDIFIER_TYPE
                                        .get()
                        )
        );

        MobGrindingUtils.BEHEADING_RECIPES.clear();
        MobGrindingUtils.BEHEADING_RECIPES.addAll(
                recipeManager
                        .recipeMap()
                        .byType(
                                MobGrindingUtils
                                        .BEHEADING_TYPE
                                        .get()
                        )
        );
    }
}
