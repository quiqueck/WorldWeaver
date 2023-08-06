package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsRandomSelect;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsRandomSelectImpl extends FeatureConfiguratorImpl<RandomFeatureConfiguration, RandomSelectorFeature> implements AsRandomSelect {
    private final List<WeightedPlacedFeature> features = new LinkedList<>();
    private Holder<PlacedFeature> defaultFeature;

    AsRandomSelectImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public AsRandomSelect add(PlacedFeatureKey feature, float weight) {
        return add(feature.getHolder(bootstrapContext), weight);
    }

    @Override
    public AsRandomSelect add(Holder<PlacedFeature> feature, float weight) {
        features.add(new WeightedPlacedFeature(feature, weight));
        return this;
    }

    @Override
    public AsRandomSelect defaultFeature(PlacedFeatureKey feature) {
        return defaultFeature(feature.getHolder(bootstrapContext));
    }

    @Override
    public AsRandomSelect defaultFeature(Holder<PlacedFeature> feature) {
        defaultFeature = feature;
        return this;
    }

    @Override
    public @NotNull RandomFeatureConfiguration createConfiguration() {
        return new RandomFeatureConfiguration(features, defaultFeature);
    }

    @Override
    protected @NotNull RandomSelectorFeature getFeature() {
        return (RandomSelectorFeature) Feature.RANDOM_SELECTOR;
    }

    public static class Key extends ConfiguredFeatureKey<AsRandomSelect> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsRandomSelect bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsRandomSelectImpl(ctx, key);
        }
    }
}
