package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.AsRandomSelect;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsRandomSelectImpl extends FeatureConfiguratorImpl implements AsRandomSelect {
    private final List<WeightedPlacedFeature> features = new LinkedList<>();
    private Holder<PlacedFeature> defaultFeature;

    AsRandomSelectImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
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
    protected @NotNull Feature createFeature() {
        // 26.3: RandomFeatureConfiguration was folded into RandomSelectorFeature.
        return new RandomSelectorFeature(features, defaultFeature);
    }

    public static class Key extends FeatureKey<AsRandomSelect> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public AsRandomSelect bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new AsRandomSelectImpl(ctx, key);
        }
    }
}
