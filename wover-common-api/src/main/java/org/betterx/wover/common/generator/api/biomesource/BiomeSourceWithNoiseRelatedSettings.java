package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public interface BiomeSourceWithNoiseRelatedSettings {
    void onLoadGeneratorSettings(NoiseGeneratorSettings generator);
}
