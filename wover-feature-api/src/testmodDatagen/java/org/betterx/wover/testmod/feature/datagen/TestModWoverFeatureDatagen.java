package org.betterx.wover.testmod.feature.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.TestModWoverFeature;

public class TestModWoverFeatureDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(ConfiguredFeaturesProvider::new);
        globalPack.addRegistryProvider(PlacedFeatureProvider::new);
        globalPack.addRegistryProvider(ModificationProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverFeature.C;
    }

}
