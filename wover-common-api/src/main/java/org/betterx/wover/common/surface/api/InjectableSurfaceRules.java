package org.betterx.wover.common.surface.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * If a ChunkGenerator implements this interface, we can use it to inject Surface
 * Rules from our SurfaceRule Registry.
 * <p>
 * This Interface is already implemented by the
 * {@link net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator}
 *
 * @param <G> Underlying ChunkGenerator type
 */
public interface InjectableSurfaceRules<G extends ChunkGenerator> {
    /**
     * Called when the Surface Rules for this BiomeSource need to be updated with
     * the ones from our SurfaceRule Registry.
     *
     * @param dimensionRegistry The Registry holding the Dimension information for this world
     * @param dimensionKey      The Dimension for which this injection is performed
     */
    void wover_injectSurfaceRules(Registry<LevelStem> dimensionRegistry, ResourceKey<LevelStem> dimensionKey);
}
