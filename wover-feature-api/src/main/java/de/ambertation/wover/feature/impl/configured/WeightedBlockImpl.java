package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.WeightedBlock;

import net.minecraft.data.worldgen.BootstrapContext;
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
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
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
        public WeightedBlock bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockImpl(ctx, key);
        }
    }
}