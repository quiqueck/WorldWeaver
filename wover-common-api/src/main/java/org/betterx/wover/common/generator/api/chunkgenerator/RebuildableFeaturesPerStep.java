package org.betterx.wover.common.generator.api.chunkgenerator;

import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * Marks a {@link ChunkGenerator} that caches its list of features grouped by generation step, and needs to be
 * told to rebuild that cache after the set of features (for example the biome's feature list) changed.
 *
 * @param <G> The concrete {@link ChunkGenerator} type
 */
public interface RebuildableFeaturesPerStep<G extends ChunkGenerator> {
    /**
     * Rebuilds the internal cache that groups features by generation step.
     */
    void wover_rebuildFeaturesPerStep();
}
