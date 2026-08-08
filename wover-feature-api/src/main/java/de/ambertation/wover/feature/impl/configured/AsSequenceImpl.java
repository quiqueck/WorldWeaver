package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.AsSequence;
import de.ambertation.wover.feature.api.features.SequenceFeature;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsSequenceImpl extends FeatureConfiguratorImpl implements AsSequence {
    private final List<Holder<PlacedFeature>> features = new LinkedList<>();

    AsSequenceImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
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
    protected @NotNull Feature createFeature() {
        if (features.isEmpty()) {
            throwStateError("Sequence must have at least one feature");
        }
        return SequenceFeature.createSequence(features);
    }

    public static class Key extends FeatureKey<AsSequence> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public AsSequence bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new AsSequenceImpl(ctx, key);
        }
    }
}
