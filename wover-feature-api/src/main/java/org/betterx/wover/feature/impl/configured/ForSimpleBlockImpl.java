package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.ForSimpleBlock;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ForSimpleBlockImpl extends FeatureConfiguratorImpl<SimpleBlockConfiguration, SimpleBlockFeature> implements ForSimpleBlock {
    private BlockStateProvider provider;

    ForSimpleBlockImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> featureKey
    ) {
        super(ctx, featureKey);
    }


    @Override
    public ForSimpleBlock block(BlockStateProvider provider) {
        this.provider = provider;
        return this;
    }


    @Override
    public ForSimpleBlock block(Block block) {
        return block(BlockStateProvider.simple(block));
    }


    @Override
    public ForSimpleBlock block(BlockState state) {
        return block(BlockStateProvider.simple(state));
    }

    /**
     * Creates a new {@link SimpleBlockConfiguration} for this builder.
     *
     * @return the configuration
     */
    @Override
    protected @NotNull SimpleBlockConfiguration createConfiguration() {
        if (this.provider == null) {
            this.throwStateError("No block state provider set.");
        }

        return new SimpleBlockConfiguration(provider);
    }

    /**
     * Gets the {@link SimpleBlockFeature} for this builder.
     *
     * @return the feature
     */
    @Override
    protected @NotNull SimpleBlockFeature getFeature() {
        return (SimpleBlockFeature) Feature.SIMPLE_BLOCK;
    }

    public static class Key extends ConfiguredFeatureKey<ForSimpleBlock> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public ForSimpleBlock bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new ForSimpleBlockImpl(ctx, key);
        }
    }
}
