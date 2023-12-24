package org.betterx.wover.testmod.biome.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.TestModWoverBiome;

public class TestModWoverBiomeDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(ModificationProvider::new);
        globalPack.addRegistryProvider(BiomeDataProvider::new);
        globalPack.addMultiProvider(BiomeProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverBiome.C;
    }

}
