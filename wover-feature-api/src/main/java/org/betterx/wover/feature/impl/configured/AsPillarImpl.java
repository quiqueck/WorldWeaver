package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.Features;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.features.PillarFeature;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsPillarImpl extends FeatureConfiguratorImpl<PillarFeatureConfig, PillarFeature> implements AsPillar {
    private IntProvider maxHeight;
    private IntProvider minHeight;
    private BlockStateProvider stateProvider;

    private PillarFeatureConfig.KnownTransformers transformer;
    private Direction direction = Direction.UP;
    private BlockPredicate allowedPlacement = BlockPredicate.ONLY_IN_AIR_PREDICATE;

    AsPillarImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public AsPillar allowedPlacement(BlockPredicate predicate) {
        this.allowedPlacement = predicate;
        return this;
    }

    @Override
    public AsPillar transformer(@NotNull PillarFeatureConfig.KnownTransformers transformer) {
        this.transformer = transformer;
        return this;
    }

    @Override
    public AsPillar direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public AsPillar blockState(Block block) {
        return blockState(BlockStateProvider.simple(block.defaultBlockState()));
    }

    @Override
    public AsPillar blockState(BlockState state) {
        return blockState(BlockStateProvider.simple(state));
    }

    @Override
    public AsPillar blockState(BlockStateProvider provider) {
        this.stateProvider = provider;
        return this;
    }

    @Override
    public AsPillar maxHeight(int max) {
        this.maxHeight = ConstantInt.of(max);
        return this;
    }

    @Override
    public AsPillar maxHeight(IntProvider max) {
        this.maxHeight = max;
        return this;
    }

    @Override
    public AsPillar minHeight(int min) {
        this.minHeight = ConstantInt.of(min);
        return this;
    }

    @Override
    public AsPillar minHeight(IntProvider min) {
        this.minHeight = min;
        return this;
    }


    @Override
    public @NotNull PillarFeatureConfig createConfiguration() {
        if (this.transformer == null) {
            throwStateError("A Pillar Features need a transformer");
        }
        if (stateProvider == null) {
            throwStateError("A Pillar Features need a stateProvider");
        }
        if (maxHeight == null) {
            throwStateError("A Pillar Features need a height");
        }
        if (minHeight == null) minHeight = ConstantInt.of(0);
        return new PillarFeatureConfig(
                minHeight,
                maxHeight,
                direction,
                allowedPlacement,
                stateProvider,
                transformer
        );
    }

    @Override
    protected @NotNull PillarFeature getFeature() {
        return (PillarFeature) Features.PILLAR;
    }

    public static class Key extends ConfiguredFeatureKey<AsPillar> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsPillar bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsPillarImpl(ctx, key);
        }
    }
}
