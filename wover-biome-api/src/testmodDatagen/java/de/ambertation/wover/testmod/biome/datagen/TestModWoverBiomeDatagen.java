package de.ambertation.wover.testmod.biome.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBiome;

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
