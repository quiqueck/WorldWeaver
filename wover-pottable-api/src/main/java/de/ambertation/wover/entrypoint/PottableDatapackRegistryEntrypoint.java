package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.pottable.impl.PottablePlantRegistryImpl;
import de.ambertation.wover.pottable.impl.PottableSoilRegistryImpl;

public class PottableDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        PottablePlantRegistryImpl.initialize();
        PottableSoilRegistryImpl.initialize();
    }
}
