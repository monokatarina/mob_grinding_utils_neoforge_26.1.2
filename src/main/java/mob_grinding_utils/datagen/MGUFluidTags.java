package mob_grinding_utils.datagen;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModTags;
import mob_grinding_utils.Reference;
import mob_grinding_utils.util.RL;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public class MGUFluidTags extends FluidTagsProvider {
    public MGUFluidTags(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generatorIn.getPackOutput(), lookupProvider, Reference.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ModTags.Fluids.EXPERIENCE).add(ModBlocks.FLUID_XP.get());
        tag(ModTags.Fluids.XPJUICE).add(ModBlocks.FLUID_XP.get());

        tag(ModTags.Fluids.EXPERIENCE).addOptionalTag(TagKey.create(Registries.FLUID, RL.rl("pneumaticcraft", "memory_essence")));
        tag(ModTags.Fluids.EXPERIENCE).addOptionalTag(TagKey.create(Registries.FLUID, RL.rl("cofh_core", "experience")));
    }
}

