package de.ambertation.wover.feature.api;

import de.ambertation.wover.feature.impl.configured.ConfiguredFeatureManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Helper methods to directly place a {@link ConfiguredFeature} in the world, outside of the normal
 * worldgen pipeline (e.g. from a block's growth logic or a command).
 * <p>
 * If the {@link ConfiguredFeature}'s underlying {@link net.minecraft.world.level.levelgen.feature.Feature}
 * implements {@link de.ambertation.wover.feature.api.features.GrowableFeature} and {@code unchanged} is
 * {@code false}, placement is delegated to
 * {@link de.ambertation.wover.feature.api.features.GrowableFeature#grow} instead of the regular
 * {@link net.minecraft.world.level.levelgen.feature.Feature#place} logic.
 */
public class FeatureUtils {
    /**
     * Places the given {@link ConfiguredFeature} in the world.
     * <p>
     * If the level is a {@link net.minecraft.server.level.ServerLevel}, the {@link ChunkGenerator}
     * is resolved automatically from the level's chunk source.
     *
     * @param feature   the feature to place
     * @param level     the level to place the feature in
     * @param pos       the position to place the feature at
     * @param random    the random source to use
     * @param unchanged if {@code true}, forces the regular {@link net.minecraft.world.level.levelgen.feature.Feature#place}
     *                  logic to be used, even if the feature implements
     *                  {@link de.ambertation.wover.feature.api.features.GrowableFeature}
     * @return {@code true} if the feature was placed, {@code false} otherwise
     */
    public static boolean placeInWorld(
            ConfiguredFeature<?, ?> feature,
            WorldGenLevel level,
            BlockPos pos,
            RandomSource random,
            boolean unchanged
    ) {
        return ConfiguredFeatureManagerImpl.placeInWorld(feature, level, pos, random, null, unchanged);
    }

    /**
     * Places the given {@link ConfiguredFeature} in the world.
     *
     * @param feature   the feature to place
     * @param level     the level to place the feature in
     * @param pos       the position to place the feature at
     * @param random    the random source to use
     * @param generator the {@link ChunkGenerator} to use for the placement
     * @param unchanged if {@code true}, forces the regular {@link net.minecraft.world.level.levelgen.feature.Feature#place}
     *                  logic to be used, even if the feature implements
     *                  {@link de.ambertation.wover.feature.api.features.GrowableFeature}
     * @return {@code true} if the feature was placed, {@code false} otherwise
     */
    public static boolean placeInWorld(
            ConfiguredFeature<?, ?> feature,
            WorldGenLevel level,
            BlockPos pos,
            RandomSource random,
            ChunkGenerator generator,
            boolean unchanged
    ) {
        return ConfiguredFeatureManagerImpl.placeInWorld(feature, level, pos, random, generator, unchanged);
    }

    private FeatureUtils() {
    }
}
