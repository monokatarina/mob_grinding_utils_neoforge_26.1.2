package mob_grinding_utils.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import java.util.concurrent.CompletableFuture;

public class MGUDamageTags implements DataProvider {
    public MGUDamageTags(PackOutput output, CompletableFuture<HolderLookup.Provider> stupid) {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "MGUDamageTags";
    }
}

