package de.ambertation.wover.generator.api.client.biomesource.client;

import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceConfig;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A client-side settings panel for the {@link BiomeSourceConfig} of a {@link BiomeSourceWithConfigScreen}
 * (e.g. the panels shown in the world-creation screen for WoVer's Nether/End biome sources).
 *
 * @param <B> the {@link BiomeSource} type the panel configures
 * @param <C> the {@link BiomeSourceConfig} type the panel configures
 */
public interface BiomeSourceConfigPanel<B extends BiomeSource, C extends BiomeSourceConfig<B>> {
    /**
     * Applies a changed configuration back to a dimension's {@link ChunkGenerator}, e.g. when the user
     * changes a setting on the panel.
     */
    @FunctionalInterface
    public static interface DimensionUpdater {
        /**
         * Applies the new {@link ChunkGenerator} to the given dimension.
         *
         * @param dimensionKey     the dimension to update
         * @param dimensionTypeKey the dimension's {@link DimensionType}
         * @param chunkGenerator   the new {@link ChunkGenerator} to apply
         */
        void updateConfiguration(
                ResourceKey<LevelStem> dimensionKey,
                ResourceKey<DimensionType> dimensionTypeKey,
                ChunkGenerator chunkGenerator
        );
    }

    /**
     * Builds the UI layout for this panel.
     *
     * @return the panel's root {@link LayoutComponent}
     */
    @Environment(EnvType.CLIENT)
    LayoutComponent<?, ?> getPanel();


    /**
     * Applies this panel's current settings to a {@link ChunkGenerator}, returning a (possibly new)
     * generator configured with them.
     *
     * @param newGenerator the generator to apply the settings to
     * @return the updated generator
     */
    ChunkGenerator updateSettings(
            ChunkGenerator newGenerator
    );
}
