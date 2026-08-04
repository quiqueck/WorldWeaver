package de.ambertation.wover.entrypoint;

import de.ambertation.wover.biome.impl.BiomeManagerImpl;
import de.ambertation.wover.biome.impl.data.BiomeCodecRegistryImpl;
import de.ambertation.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import de.ambertation.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import de.ambertation.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class LibWoverBiome implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-biome", "wover");

    @Override
    public void onInitialize() {
        BiomeManagerImpl.initialize();
        BiomeCodecRegistryImpl.initialize();
        //BiomeDataRegistryImpl.initialize(); //done in the wover.datapack.registry entrypoint
        BiomePredicateRegistryImpl.initialize();
        BiomeModificationRegistryImpl.initialize();
    }
}