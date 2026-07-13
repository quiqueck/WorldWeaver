package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.biome.BiomeSource;

/**
 * The configuration of a {@link BiomeSourceWithConfig}.
 * <p>
 * Implementations hold the settings that were used to create a {@link BiomeSource} (for example the set of
 * possible biomes) and are used to decide whether a new configuration can be applied to an existing
 * {@link net.minecraft.world.level.chunk.ChunkGenerator} in place, or whether the world needs to be repaired
 * (chunks re-evaluated) because the change is not compatible with already generated terrain.
 *
 * @param <B> The type of {@link BiomeSource} this configuration belongs to
 */
public interface BiomeSourceConfig<B extends BiomeSource> {
    /**
     * Checks if the passed in configuration can be applied to the current one without the need to repair
     * (re-evaluate) already generated chunks.
     *
     * @param input The new configuration that should be applied
     * @return {@code true} if {@code input} can be applied without repairing the world
     */
    boolean couldSetWithoutRepair(BiomeSourceConfig<?> input);

    /**
     * Checks if the passed in configuration describes the same settings as this one.
     *
     * @param input The configuration to compare against
     * @return {@code true} if both configurations are equivalent
     */
    boolean sameConfig(BiomeSourceConfig<?> input);
}
