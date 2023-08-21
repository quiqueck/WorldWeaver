package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.biome.BiomeSource;

public interface BiomeSourceConfig<B extends BiomeSource> {
    boolean couldSetWithoutRepair(BiomeSourceConfig<?> input);
    boolean sameConfig(BiomeSourceConfig<?> input);
}
