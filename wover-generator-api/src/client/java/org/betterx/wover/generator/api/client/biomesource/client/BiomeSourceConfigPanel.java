package org.betterx.wover.generator.api.client.biomesource.client;

import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface BiomeSourceConfigPanel<B extends BiomeSource, C extends BiomeSourceConfig<B>> {
    @FunctionalInterface
    public static interface DimensionUpdater {
        void updateConfiguration(
                ResourceKey<LevelStem> dimensionKey,
                ResourceKey<DimensionType> dimensionTypeKey,
                ChunkGenerator chunkGenerator
        );
    }

    @Environment(EnvType.CLIENT)
    LayoutComponent<?, ?> getPanel();


    ChunkGenerator updateSettings(
            ChunkGenerator newGenerator
    );
}
