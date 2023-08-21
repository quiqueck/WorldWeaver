package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.biome.BiomeSource;

public interface BiomeSourceWithConfig<B extends BiomeSource, C extends BiomeSourceConfig<B>> {
    C getBiomeSourceConfig();
    void setBiomeSourceConfig(C newConfig);
}
