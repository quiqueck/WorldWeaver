package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.util.function.TriConsumer;

/**
 * Distributes the Biomes of a {@link WoverBiomePicker} spatially, splitting the world into a grid of
 * {@link BiomeChunk}s to cache the picked Biomes.
 * <p>
 * Implementations built into WoVer are {@link org.betterx.wover.generator.impl.map.hex.HexBiomeMap} (a
 * hex-grid distribution) and {@link org.betterx.wover.generator.impl.map.square.SquareBiomeMap} (a
 * square-grid distribution); use a {@link MapBuilderFunction} (for example
 * {@link org.betterx.wover.generator.api.biomesource.end.WoverEndConfig.EndBiomeMapType#mapBuilder} or
 * {@link org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig.NetherBiomeMapType#mapBuilder})
 * to construct one.
 */
public interface BiomeMap {
    /**
     * Registers a callback that is notified whenever a new {@link BiomeChunk} is generated.
     *
     * @param processor called with the chunk's coordinates and its side length (see
     *                  {@link BiomeChunk#getSide()})
     */
    void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor);

    /**
     * Returns the {@link BiomeChunk} at the given chunk coordinates, generating it first if it does not
     * exist yet.
     *
     * @param cx     the chunk's x coordinate
     * @param cz     the chunk's z coordinate
     * @param update if {@code true}, forces the chunk to be regenerated even if it was cached before
     * @return the chunk
     */
    BiomeChunk getChunk(int cx, int cz, boolean update);

    /**
     * Picks the Biome for a block position.
     *
     * @param x the block x coordinate
     * @param y the block y coordinate
     * @param z the block z coordinate
     * @return the picked Biome
     */
    WoverBiomePicker.PickableBiome getBiome(double x, double y, double z);

    /**
     * Clears every cached {@link BiomeChunk}, forcing them to be regenerated on next access.
     */
    void clearCache();
}
