package org.betterx.wover.entrypoint;

import org.betterx.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import org.betterx.wover.biome.impl.modification.predicates.BiomePredicateRegistryImpl;
import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverBiome implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-biome", "wover");

    @Override
    public void onInitialize() {
        BiomePredicateRegistryImpl.initialize();
        BiomeModificationRegistryImpl.initialize();
    }
}