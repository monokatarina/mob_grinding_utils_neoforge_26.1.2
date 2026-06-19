package mob_grinding_utils.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import java.util.concurrent.CompletableFuture;

public class MGUBlockStates implements DataProvider {
    public MGUBlockStates(DataGenerator gen) {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "MGUBlockStates";
    }
}

