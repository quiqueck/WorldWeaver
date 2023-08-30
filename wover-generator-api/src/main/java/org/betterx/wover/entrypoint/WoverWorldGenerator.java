package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGeneratorImpl;
import org.betterx.wover.generator.impl.preset.PresetRegistryImpl;

import net.fabricmc.api.ModInitializer;

public class WoverWorldGenerator implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-generator", "wover");

    @Override
    public void onInitialize() {
        PresetRegistryImpl.ensureStaticallyLoaded();
        WoverBiomeDataImpl.initialize();
        BiomeSourceManagerImpl.initialize();
        ChunkGeneratorManagerImpl.initialize();
        WoverChunkGeneratorImpl.initialize();
    }
}