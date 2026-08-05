package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.generator.api.preset.WorldPresets;
import de.ambertation.wover.generator.impl.biomesource.BiomeSourceManagerImpl;
import de.ambertation.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import de.ambertation.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;
import de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGeneratorImpl;
import de.ambertation.wover.generator.impl.preset.PresetRegistryImpl;
import de.ambertation.wover.preset.api.WorldPresetManager;

import net.fabricmc.api.ModInitializer;

public class LibWoverWorldGenerator implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-generator", "wover");

    @Override
    public void onInitialize() {
        // A dedicated server with no "level-type" in its server.properties should come up on our
        // preset - DedicatedServerPropertiesMixin writes this default into a freshly generated file.
        // This only decides the *default*; a level-type that is present is always honoured.
        if (!ModCore.isClient()) {
            WorldPresetManager.suggestDefault(WorldPresets.WOVER_WORLD, 2000);
        }

        PresetRegistryImpl.ensureStaticallyLoaded();
        WoverBiomeDataImpl.initialize();
        BiomeSourceManagerImpl.initialize();
        ChunkGeneratorManagerImpl.initialize();
        WoverChunkGeneratorImpl.initialize();
    }
}