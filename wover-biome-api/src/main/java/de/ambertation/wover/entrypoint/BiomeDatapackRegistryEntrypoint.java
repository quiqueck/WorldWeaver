package de.ambertation.wover.entrypoint;

import de.ambertation.wover.biome.impl.BiomeManagerImpl;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.biome.impl.modification.BiomeModificationRegistryImpl;
import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;

public class BiomeDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        BiomeManagerImpl.initialize();
        BiomeDataRegistryImpl.initialize();
        BiomeModificationRegistryImpl.initialize();
    }
}
