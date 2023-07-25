package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.builders.FeatureConfigurator;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class PlacedFeatureManager {
    public static PlacedFeatureKey createKey(ResourceLocation id) {
        return new PlacedFeatureKey(id);
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>, B extends FeatureConfigurator<FC, F>> PlacedFeatureKey.WithConfigured
    createKey(
            ResourceLocation id,
            ResourceKey<ConfiguredFeature<?, ?>> configured
    ) {
        return new PlacedFeatureKey.WithConfigured(id, configured);
    }


    public static <B extends FeatureConfigurator<?, ?>> PlacedFeatureKey.WithConfigured
    createKey(
            ResourceLocation id,
            ConfiguredFeatureKey<B> configuredFeatureKey
    ) {
        return new PlacedFeatureKey.WithConfigured(id, configuredFeatureKey);
    }

    public static <B extends FeatureConfigurator<?, ?>> PlacedFeatureKey.WithConfigured
    createKey(
            ConfiguredFeatureKey<B> configuredFeatureKey
    ) {
        return new PlacedFeatureKey.WithConfigured(configuredFeatureKey.key.location(), configuredFeatureKey);
    }
}
