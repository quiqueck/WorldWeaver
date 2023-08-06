package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsOre;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsOreImpl extends FeatureConfiguratorImpl<OreConfiguration, OreFeature> implements AsOre {
    private final List<OreConfiguration.TargetBlockState> targetStates = new LinkedList<>();
    private int size = 6;
    private float discardChanceOnAirExposure = 0;

    AsOreImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> featureKey
    ) {
        super(ctx, featureKey);
    }

    @Override
    public AsOre add(Block containedIn, Block ore) {
        return this.add(containedIn, ore.defaultBlockState());
    }

    @Override
    public AsOre add(Block containedIn, BlockState ore) {
        return this.add(new BlockMatchTest(containedIn), ore);
    }

    @Override
    public AsOre add(RuleTest containedIn, Block ore) {
        return this.add(containedIn, ore.defaultBlockState());
    }

    @Override
    public AsOre add(RuleTest containedIn, BlockState ore) {
        targetStates.add(OreConfiguration.target(
                containedIn,
                ore
        ));
        return this;
    }

    @Override
    public AsOre veinSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public AsOre discardChanceOnAirExposure(float chance) {
        this.discardChanceOnAirExposure = chance;
        return this;
    }

    @Override
    public @NotNull OreConfiguration createConfiguration() {
        return new OreConfiguration(targetStates, size, discardChanceOnAirExposure);
    }

    @Override
    protected @NotNull OreFeature getFeature() {
        return (OreFeature) Feature.ORE;
    }

    public static class Key extends ConfiguredFeatureKey<AsOre> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsOre bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsOreImpl(ctx, key);
        }
    }
}
