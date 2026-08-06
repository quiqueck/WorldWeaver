package de.ambertation.wover.surface.api.conditions;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.function.Supplier;

/**
 * This interface is used to provide access to the surface rule context.
 */
public interface SurfaceRulesContext {
    /**
     * Returns the x-coordinate for the evaluated block
     *
     * @return the block x coordinate
     */
    int getBlockX();
    /**
     * Returns the y-coordinate for the evaluated block
     *
     * @return the block y coordinate
     */
    int getBlockY();
    /**
     * Returns the z-coordinate for the evaluated block
     *
     * @return the block z coordinate
     */
    int getBlockZ();

    /**
     * The depth of the surface at the given block
     *
     * @return the depth of the surface
     */
    int getSurfaceDepth();
    /**
     * Returns the biome for the evaluated block
     * <p>
     * This used to hand out a {@link Supplier} because up to 26.1 that is literally what the vanilla
     * surface rule context kept back then. 26.2 resolves the biome lazily inside the context itself, so
     * this now hands out the {@link Holder} directly - the lookup still only happens on the first call
     * for a given position.
     *
     * @return the biome
     */
    Holder<Biome> getBiome();
    /**
     * Returns the chunk for the evaluated block
     *
     * @return the chunk
     */
    ChunkAccess getChunk();
    /**
     * Returns the noise chunk for the evaluated block
     *
     * @return the noise chunk
     */
    NoiseChunk getNoiseChunk();
    /**
     * Returns the depth of the stone above the evaluated block
     *
     * @return the depth of the stone above
     */
    int getStoneDepthAbove();
    /**
     * Returns the depth of the stone below the evaluated block
     *
     * @return the depth of the stone below
     */
    int getStoneDepthBelow();
    /**
     * Returns the last update y-coordinate
     *
     * @return the last update y-coordinate
     */
    long getLastUpdateY();
    /**
     * Returns the last update xz-coordinate
     *
     * @return the last update xz-coordinate
     */
    long getLastUpdateXZ();
    /**
     * Returns the random state for the surface rules
     *
     * @return the random state
     */
    RandomState getRandomState();
}
