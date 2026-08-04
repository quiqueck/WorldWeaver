package de.ambertation.wover.testmod.generator.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverWorldGenerator;

public class TestModWoverWorldGeneratorDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addMultiProvider(BiomeProvider::new);
        globalPack.addMultiProvider(FeatureProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverWorldGenerator.C;
    }

}
