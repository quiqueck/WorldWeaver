package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.util.function.TriConsumer;

public interface BiomeMap {
    void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor);
    BiomeChunk getChunk(int cx, int cz, boolean update);
    WoverBiomePicker.PickableBiome getBiome(double x, double y, double z);
    void clearCache();
}
