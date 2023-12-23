package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.features.GrowableFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

import java.util.Optional;

public class ConfiguredFeatureManagerImpl {
    public static boolean placeInWorld(
            ConfiguredFeature<?, ?> feature,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            boolean unchanged
    ) {
        return placeUnboundInWorld(feature.feature(), feature.config(), level, pos, random, unchanged);
    }

    private static boolean placeUnboundInWorld(
            Feature<?> feature,
            FeatureConfiguration config,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
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

        FeaturePlaceContext context = new FeaturePlaceContext(
                Optional.empty(),
                level,
                level.getChunkSource().getGenerator(),
                random,
                pos,
                config
        );

        return feature.place(context);
    }
}
