package de.ambertation.wover.common.generator.api.biomesource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Marks a {@link net.minecraft.world.level.chunk.ChunkGenerator} that can expose the
 * {@link NoiseGeneratorSettings} it was created with.
 */
public interface NoiseGeneratorSettingsProvider {
    /**
     * Gets the {@link NoiseGeneratorSettings} used by this generator.
     *
     * @return The current noise generator settings
     */
    NoiseGeneratorSettings wover_getNoiseGeneratorSettings();

    /**
     * Gets the {@link Holder} for the {@link NoiseGeneratorSettings} used by this generator.
     *
     * @return The holder wrapping the current noise generator settings
     */
    Holder<NoiseGeneratorSettings> wover_getNoiseGeneratorSettingHolders();
}