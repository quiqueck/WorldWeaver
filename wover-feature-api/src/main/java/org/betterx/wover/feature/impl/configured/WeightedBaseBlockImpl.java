package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.configurators.BaseWeightedBlock;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public abstract class WeightedBaseBlockImpl<FC extends FeatureConfiguration, F extends Feature<FC>, W extends BaseWeightedBlock<FC, F, W>> extends FeatureConfiguratorImpl<FC, F> implements BaseWeightedBlock<FC, F, W> {
    SimpleWeightedRandomList.Builder<BlockState> stateBuilder = SimpleWeightedRandomList.builder();

    WeightedBaseBlockImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public W add(Block block, int weight) {
        return add(block.defaultBlockState(), weight);
    }

    @Override
    public W add(BlockState state, int weight) {
        stateBuilder.add(state, weight);
        return (W) this;
    }

    @Override
    public W addAllStates(Block block, int weight) {
        Set<BlockState> states = BlockHelper.getPossibleStates(block);
        states.forEach(s -> add(block.defaultBlockState(), Math.max(1, weight / states.size())));
        return (W) this;
    }

    @Override
    public W addAllStatesFor(IntegerProperty prop, Block block, int weight) {
        Collection<Integer> values = prop.getPossibleValues();
        values.forEach(s -> add(block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
        return (W) this;
    }
}
