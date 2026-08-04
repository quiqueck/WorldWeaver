package de.ambertation.wover.generator.api.map;

import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;

/**
 * A single, square chunk of a {@link BiomeMap}'s biome grid, caching the picked
 * {@link WoverBiomePicker.PickableBiome} for each cell.
 * <p>
 * Coordinates are local to the chunk, in the range {@code [0, getSide())}.
 */
public interface BiomeChunk {
    /**
     * Caches the Biome picked for a cell of this chunk.
     *
     * @param x     the local x coordinate, in {@code [0, getSide())}
     * @param z     the local z coordinate, in {@code [0, getSide())}
     * @param biome the picked Biome
     */
    void setBiome(int x, int z, WoverBiomePicker.PickableBiome biome);

    /**
     * Returns the Biome cached for a cell of this chunk.
     *
     * @param x the local x coordinate, in {@code [0, getSide())}
     * @param z the local z coordinate, in {@code [0, getSide())}
     * @return the cached Biome, or {@code null} if none was set yet
     */
    WoverBiomePicker.PickableBiome getBiome(int x, int z);

    /**
     * The side length of this (square) chunk, in cells.
     *
     * @return the side length
     */
    int getSide();
}
