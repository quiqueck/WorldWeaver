package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.feature.impl.configured.FeatureConfiguratorImpl;
import de.ambertation.wover.feature.impl.placed.PlacedFeatureManagerImpl;

public class FeatureDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        FeatureConfiguratorImpl.initialize();
        PlacedFeatureManagerImpl.initialize();
    }
}
