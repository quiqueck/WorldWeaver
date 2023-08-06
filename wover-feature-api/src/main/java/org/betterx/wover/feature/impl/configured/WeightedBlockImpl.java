package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlock;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeightedBlockImpl extends WeightedBaseBlockImpl<SimpleBlockConfiguration, SimpleBlockFeature, WeightedBlock> implements WeightedBlock {
    WeightedBlockImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public @NotNull SimpleBlockConfiguration createConfiguration() {
        return new SimpleBlockConfiguration(new WeightedStateProvider(stateBuilder.build()));
    }

    @Override
    protected @NotNull SimpleBlockFeature getFeature() {
        return (SimpleBlockFeature) Feature.SIMPLE_BLOCK;
    }

    public static class Key extends ConfiguredFeatureKey<WeightedBlock> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public WeightedBlock bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockImpl(ctx, key);
        }
    }
}