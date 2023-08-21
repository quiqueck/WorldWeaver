package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;

public interface BiomeChunk {
    void setBiome(int x, int z, WoverBiomePicker.PickableBiome biome);
    WoverBiomePicker.PickableBiome getBiome(int x, int z);
    int getSide();
}
