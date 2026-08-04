package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.surface.impl.SurfaceRuleRegistryImpl;

public class SurfaceDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        SurfaceRuleRegistryImpl.initialize();
    }
}
