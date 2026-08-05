package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.features.GrowableFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class ConfiguredFeatureManagerImpl {
    public static boolean placeInWorld(
            ConfiguredFeature<?, ?> feature,
            WorldGenLevel level,
            BlockPos pos,
            RandomSource random,
            @Nullable ChunkGenerator chunkGenerator,
            boolean unchanged
    ) {
        return placeUnboundInWorld(feature.feature(), feature.config(), level, pos, random, chunkGenerator, unchanged);
    }

    private static boolean placeUnboundInWorld(
            Feature<?> feature,
            FeatureConfiguration config,
            WorldGenLevel level,
            BlockPos pos,
            RandomSource random,
            @Nullable ChunkGenerator chunkGenerator,
            boolean asIs
    ) {
        if (!asIs) {
            if (config instanceof RandomPatchConfiguration rnd) {
                var configured = rnd.feature().value().feature().value();
                feature = configured.feature();
                config = configured.config();
            }

            if (feature instanceof GrowableFeature growable) {
                return growable.grow(level, pos, random, config);
            }
        }
        
        if (chunkGenerator == null && level instanceof ServerLevel sLevel) {
            chunkGenerator = sLevel.getChunkSource().getGenerator();
        }

        FeaturePlaceContext context = new FeaturePlaceContext(
                Optional.empty(),
                level,
                chunkGenerator,
                random,
                pos,
                config
        );

        return feature.place(context);
    }
}
