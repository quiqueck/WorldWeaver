package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.registry.DatapackRegistryEntrypoint;
import org.betterx.wover.pottable.impl.PottablePlantRegistryImpl;
import org.betterx.wover.pottable.impl.PottableSoilRegistryImpl;

public class PottableDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        PottablePlantRegistryImpl.initialize();
        PottableSoilRegistryImpl.initialize();
    }
}
