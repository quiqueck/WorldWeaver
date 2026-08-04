package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.Features;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.AsSequence;
import de.ambertation.wover.feature.api.features.SequenceFeature;
import de.ambertation.wover.feature.api.features.config.SequenceFeatureConfig;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsSequenceImpl extends FeatureConfiguratorImpl<SequenceFeatureConfig, SequenceFeature> implements AsSequence {
    private final List<Holder<PlacedFeature>> features = new LinkedList<>();

    AsSequenceImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public AsSequence add(PlacedFeatureKey featureKey) {
        features.add(featureKey.getHolder(bootstrapContext != null
                ? bootstrapContext
                : getTransitiveBootstrapContext()));
        return this;
    }

    @Override
    public AsSequence add(Holder<PlacedFeature> holder) {
        features.add(holder);
        return this;
    }

    @Override
    public @NotNull SequenceFeatureConfig createConfiguration() {
        if (features.isEmpty()) {
            throwStateError("Sequence must have at least one feature");
        }
        return SequenceFeatureConfig.createSequence(features);
    }

    @Override
    protected @NotNull SequenceFeature getFeature() {
        return (SequenceFeature) Features.SEQUENCE;
    }

    public static class Key extends ConfiguredFeatureKey<AsSequence> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsSequence bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsSequenceImpl(ctx, key);
        }
    }
}
