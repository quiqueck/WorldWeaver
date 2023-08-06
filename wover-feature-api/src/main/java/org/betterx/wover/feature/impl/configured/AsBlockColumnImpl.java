package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockProperties;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.AsBlockColumn;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsBlockColumnImpl extends FeatureConfiguratorImpl<BlockColumnConfiguration, BlockColumnFeature> implements org.betterx.wover.feature.api.configured.configurators.AsBlockColumn {
    private final List<BlockColumnConfiguration.Layer> layers = new LinkedList<>();
    private Direction direction = Direction.UP;
    private BlockPredicate allowedPlacement = BlockPredicate.ONLY_IN_AIR_PREDICATE;
    private boolean prioritizeTip = false;

    AsBlockColumnImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public AsBlockColumn add(int height, Block block) {
        return add(ConstantInt.of(height), BlockStateProvider.simple(block));
    }

    @Override
    public AsBlockColumn add(int height, BlockState state) {
        return add(ConstantInt.of(height), BlockStateProvider.simple(state));
    }

    @Override
    public AsBlockColumn add(int height, BlockStateProvider state) {
        return add(ConstantInt.of(height), state);
    }

    @Override
    public final AsBlockColumn addRandom(int height, BlockState... states) {
        return this.addRandom(ConstantInt.of(height), states);
    }

    @Override
    public final AsBlockColumn addRandom(IntProvider height, BlockState... states) {
        var builder = SimpleWeightedRandomList.<BlockState>builder();
        for (BlockState state : states) builder.add(state, 1);
        return add(height, new WeightedStateProvider(builder.build()));
    }

    @Override
    public AsBlockColumn add(IntProvider height, Block block) {
        return add(height, BlockStateProvider.simple(block));
    }

    @Override
    public AsBlockColumn add(IntProvider height, BlockState state) {
        return add(height, BlockStateProvider.simple(state));
    }

    @Override
    public AsBlockColumn add(IntProvider height, BlockStateProvider state) {
        layers.add(new BlockColumnConfiguration.Layer(height, state));
        return this;
    }

    @Override
    public AsBlockColumn addTripleShape(BlockState state, IntProvider midHeight) {
        return this
                .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM))
                .add(midHeight, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE))
                .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP));
    }

    @Override
    public AsBlockColumn addTripleShapeUpsideDown(BlockState state, IntProvider midHeight) {
        return this
                .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP))
                .add(midHeight, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE))
                .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM));
    }

    @Override
    public AsBlockColumn addBottomShapeUpsideDown(BlockState state, IntProvider midHeight) {
        return this
                .add(midHeight, state.setValue(BlockProperties.BOTTOM, false))
                .add(1, state.setValue(BlockProperties.BOTTOM, true));
    }

    @Override
    public AsBlockColumn addBottomShape(BlockState state, IntProvider midHeight) {
        return this
                .add(1, state.setValue(BlockProperties.BOTTOM, true))
                .add(midHeight, state.setValue(BlockProperties.BOTTOM, false));
    }

    @Override
    public AsBlockColumn addTopShapeUpsideDown(BlockState state, IntProvider midHeight) {
        return this
                .add(1, state.setValue(BlockProperties.TOP, true))
                .add(midHeight, state.setValue(BlockProperties.TOP, false));
    }

    @Override
    public AsBlockColumn addTopShape(BlockState state, IntProvider midHeight) {
        return this
                .add(midHeight, state.setValue(BlockProperties.TOP, false))
                .add(1, state.setValue(BlockProperties.TOP, true));
    }

    @Override
    public AsBlockColumn direction(Direction dir) {
        direction = dir;
        return this;
    }

    @Override
    public AsBlockColumn prioritizeTip() {
        return this.prioritizeTip(true);
    }

    @Override
    public AsBlockColumn prioritizeTip(boolean prio) {
        prioritizeTip = prio;
        return this;
    }

    @Override
    public AsBlockColumn allowedPlacement(BlockPredicate predicate) {
        allowedPlacement = predicate;
        return this;
    }

    @Override
    public BlockColumnConfiguration createConfiguration() {
        return new BlockColumnConfiguration(layers, direction, allowedPlacement, prioritizeTip);
    }

    @Override
    protected @NotNull BlockColumnFeature getFeature() {
        return (BlockColumnFeature) Feature.BLOCK_COLUMN;
    }

    public static class Key extends ConfiguredFeatureKey<AsBlockColumn> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsBlockColumn bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsBlockColumnImpl(ctx, key);
        }
    }
}
