package org.betterx.wover.generator.impl.chunkgenerator;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public interface ConfiguredChunkGenerator {
    ResourceKey<WorldPreset> wover_getConfiguredWorldPreset();

    void wover_setConfiguredWorldPreset(ResourceKey<WorldPreset> preset);
}
