package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.world.level.biome.BiomeSource;

/**
 * Marks a {@link BiomeSource} that is driven by a {@link BiomeSourceConfig}.
 *
 * @param <B> The type of {@link BiomeSource} that is configured
 * @param <C> The type of {@link BiomeSourceConfig} used to configure {@code B}
 */
public interface BiomeSourceWithConfig<B extends BiomeSource, C extends BiomeSourceConfig<B>> {
    /**
     * Gets the configuration currently applied to this {@link BiomeSource}.
     *
     * @return The current configuration
     */
    C getBiomeSourceConfig();

    /**
     * Applies a new configuration to this {@link BiomeSource}.
     *
     * @param newConfig The configuration that should be applied
     */
    void setBiomeSourceConfig(C newConfig);
}
