package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Marks a {@link net.minecraft.world.level.biome.BiomeSource} that needs to react to the
 * {@link NoiseGeneratorSettings} of the world it is used in (for example to adjust biome selection based on
 * sea level or noise router settings).
 */
public interface BiomeSourceWithNoiseRelatedSettings {
    /**
     * Called once the {@link NoiseGeneratorSettings} for the current world are known.
     *
     * @param generator The active noise generator settings
     */
    void onLoadGeneratorSettings(NoiseGeneratorSettings generator);
}
