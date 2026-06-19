package mob_grinding_utils.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class Generator {

    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        var lookupProvider = event.getLookupProvider();
        var packOutput = generator.getPackOutput();

        generator.addProvider(
                true,
                new RecipeProvider.Runner(
                        packOutput,
                        lookupProvider
                ) {
                    @Override
                    protected RecipeProvider createRecipeProvider(
                            HolderLookup.Provider registries,
                            RecipeOutput output
                    ) {
                        return new Recipes(
                                registries,
                                output
                        );
                    }

                    @Override
                    public String getName() {
                        return "Mob Grinding Utils Recipes";
                    }
                }
        );

        generator.addProvider(
                true,
                new MGUBlockTags(
                        generator,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                MGULootTables.getProvider(
                        packOutput,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                new MGUFluidTags(
                        generator,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                new MGUEntityTypeTags(
                        generator,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                new MGUBlockStates(
                        generator
                )
        );

        generator.addProvider(
                true,
                new MGUBiomeTags(
                        generator,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                new MGUDamageType(
                        packOutput,
                        lookupProvider
                )
        );

        generator.addProvider(
                true,
                new MGUDamageTags(
                        packOutput,
                        lookupProvider
                )
        );
    }
}

