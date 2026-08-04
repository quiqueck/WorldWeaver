package de.ambertation.wover.common.generator.api.chunkgenerator;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * Marks a {@link ChunkGenerator} whose {@link net.minecraft.world.level.biome.BiomeSource} may have been merged
 * or replaced (see {@link de.ambertation.wover.common.generator.api.biomesource.MergeableBiomeSource}) and can be
 * restored back to the biome source it was originally created with.
 *
 * @param <B> The concrete {@link ChunkGenerator} type
 */
public interface RestorableBiomeSource<B extends ChunkGenerator> {
    /**
     * Restores the original, unmerged {@link net.minecraft.world.level.biome.BiomeSource} of this generator.
     *
     * @param dimensionKey The dimension this generator is used in
     */
    void restoreInitialBiomeSource(ResourceKey<LevelStem> dimensionKey);
}
