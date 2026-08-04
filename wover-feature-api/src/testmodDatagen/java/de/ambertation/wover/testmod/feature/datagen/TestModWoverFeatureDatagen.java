package de.ambertation.wover.testmod.feature.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverFeature;

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
