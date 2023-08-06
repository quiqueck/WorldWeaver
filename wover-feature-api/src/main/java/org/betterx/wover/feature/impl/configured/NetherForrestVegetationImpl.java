package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.NetherForrestVegetation;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherForrestVegetationImpl extends FeatureConfiguratorImpl<NetherForestVegetationConfig, NetherForestVegetationFeature> implements org.betterx.wover.feature.api.configured.configurators.NetherForrestVegetation {
    private SimpleWeightedRandomList.Builder<BlockState> blocks;
    private WeightedStateProvider stateProvider;
    private int spreadWidth = 8;
    private int spreadHeight = 4;

    NetherForrestVegetationImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }


    @Override
    public NetherForrestVegetation spreadWidth(int width) {
        spreadWidth = width;
        return this;
    }

    @Override
    public NetherForrestVegetation spreadHeight(int height) {
        spreadHeight = height;
        return this;
    }

    @Override
    public NetherForrestVegetation addAllStates(Block block, int weight) {
        Set<BlockState> states = BlockHelper.getPossibleStates(block);
        states.forEach(s -> add(block.defaultBlockState(), Math.max(1, weight / states.size())));
        return this;
    }

    @Override
    public NetherForrestVegetation addAllStatesFor(IntegerProperty prop, Block block, int weight) {
        Collection<Integer> values = prop.getPossibleValues();
        values.forEach(s -> add(block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
        return this;
    }

    @Override
    public NetherForrestVegetation add(Block block, int weight) {
        return add(block.defaultBlockState(), weight);
    }

    @Override
    public NetherForrestVegetation add(BlockState state, int weight) {
        if (stateProvider != null) {
            throw new IllegalStateException("You can not add new state once a WeightedStateProvider was built. (" + state + ", " + weight + ")");
        }
        if (blocks == null) {
            blocks = SimpleWeightedRandomList.builder();
        }
        blocks.add(state, weight);
        return this;
    }

    @Override
    public NetherForrestVegetation provider(WeightedStateProvider provider) {
        if (blocks != null) {
            throwStateError(
                    "You can not set a WeightedStateProvider after states were added manually.");
        }
        stateProvider = provider;
        return this;
    }

    @Override
    public NetherForestVegetationConfig createConfiguration() {
        if (stateProvider == null && blocks == null) {
            throwStateError("NetherForestVegetationConfig needs at least one BlockState");
        }
        if (stateProvider == null) stateProvider = new WeightedStateProvider(blocks.build());
        return new NetherForestVegetationConfig(stateProvider, spreadWidth, spreadHeight);
    }

    @Override
    protected @NotNull NetherForestVegetationFeature getFeature() {
        return (NetherForestVegetationFeature) Feature.NETHER_FOREST_VEGETATION;
    }

    public static class Key extends ConfiguredFeatureKey<NetherForrestVegetation> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public NetherForrestVegetation bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new NetherForrestVegetationImpl(ctx, key);
        }
    }
}
