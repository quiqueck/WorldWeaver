package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.WeightedBlock;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeightedBlockImpl extends WeightedBaseBlockImpl<WeightedBlock> implements WeightedBlock {
    WeightedBlockImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
    ) {
        super(ctx, key);
    }

    @Override
    protected @NotNull Feature createFeature() {
        return new SimpleBlockFeature(new WeightedStateProvider(stateBuilder.build()));
    }

    public static class Key extends FeatureKey<WeightedBlock> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public WeightedBlock bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new WeightedBlockImpl(ctx, key);
        }
    }
}
