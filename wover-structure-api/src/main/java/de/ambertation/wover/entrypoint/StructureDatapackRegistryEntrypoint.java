package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.structure.impl.StructureManagerImpl;
import de.ambertation.wover.structure.impl.pools.StructurePoolManagerImpl;
import de.ambertation.wover.structure.impl.sets.StructureSetManagerImpl;

public class StructureDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        StructurePoolManagerImpl.initialize();
        StructureManagerImpl.initialize();
        StructureSetManagerImpl.initialize();
    }
}
