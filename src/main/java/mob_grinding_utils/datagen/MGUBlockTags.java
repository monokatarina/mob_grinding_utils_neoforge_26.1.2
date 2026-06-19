package mob_grinding_utils.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import java.util.concurrent.CompletableFuture;

public class MGUBlockTags implements DataProvider {
    public MGUBlockTags(DataGenerator generatorIn, CompletableFuture<HolderLookup.Provider> something) {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "MGUBlockTags";
    }
}

