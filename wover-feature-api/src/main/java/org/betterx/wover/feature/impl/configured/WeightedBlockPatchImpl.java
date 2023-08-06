package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.WeightedBlockPatch;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeightedBlockPatchImpl extends WeightedBaseBlockImpl<RandomPatchConfiguration, RandomPatchFeature, WeightedBlockPatch> implements WeightedBlockPatch {

    private BlockPredicate groundType = null;
    private boolean isEmpty = true;
    private int tries = 96;
    private int xzSpread = 7;
    private int ySpread = 3;

    WeightedBlockPatchImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }


    @Override
    public WeightedBlockPatch isEmpty() {
        return this.isEmpty(true);
    }

    @Override
    public WeightedBlockPatch isEmpty(boolean value) {
        this.isEmpty = value;
        return this;
    }

    @Override
    public WeightedBlockPatch isOn(BlockPredicate predicate) {
        this.groundType = predicate;
        return this;
    }

    @Override
    public WeightedBlockPatch isEmptyAndOn(BlockPredicate predicate) {
        return this.isEmpty().isOn(predicate);
    }

    @Override
    public WeightedBlockPatch likeDefaultNetherVegetation() {
        return likeDefaultNetherVegetation(8, 4);
    }

    @Override
    public WeightedBlockPatch likeDefaultNetherVegetation(int xzSpread, int ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
        tries = xzSpread * xzSpread;
        return this;
    }


    @Override
    public WeightedBlockPatch tries(int v) {
        tries = v;
        return this;
    }

    @Override
    public WeightedBlockPatch spreadXZ(int v) {
        xzSpread = v;
        return this;
    }

    @Override
    public WeightedBlockPatch spreadY(int v) {
        ySpread = v;
        return this;
    }

    @Override
    public @NotNull RandomPatchConfiguration createConfiguration() {
        var blockFeature = ConfiguredFeatureManager
                .INLINE_BUILDER
                .simple()
                .block((new WeightedStateProvider(stateBuilder.build())))
                .inlinePlace();

        if (isEmpty) blockFeature.isEmpty();
        if (groundType != null) blockFeature.isOn(groundType);

        return new RandomPatchConfiguration(tries, xzSpread, ySpread, blockFeature.directHolder());
    }

    @Override
    protected @NotNull RandomPatchFeature getFeature() {
        return (RandomPatchFeature) Feature.RANDOM_PATCH;
    }


    public static class Key extends ConfiguredFeatureKey<WeightedBlockPatch> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public WeightedBlockPatch bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockPatchImpl(ctx, key);
        }
    }

    public static class KeyBonemeal extends ConfiguredFeatureKey<WeightedBlockPatch> {
        public KeyBonemeal(ResourceLocation id) {
            super(id);
        }

        @Override
        public WeightedBlockPatch bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockPatchImpl(ctx, key).likeDefaultBonemeal();
        }
    }
}
