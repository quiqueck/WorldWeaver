package org.betterx.wover.feature.api.features.config;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

/**
 * Abstract Configuration for {@link org.betterx.wover.feature.api.features.PlaceBlockFeature}s.
 */
public abstract class PlaceBlockFeatureConfig implements FeatureConfiguration {

    /**
     * Codec for {@link PlaceBlockFeatureConfig}.
     */
    protected static <T extends PlaceBlockFeatureConfig> RecordCodecBuilder<T, BlockStateProvider> blockStateCodec() {
        return BlockStateProvider.CODEC
                .fieldOf("entries")
                .forGetter((T o) -> o.stateProvider);
    }

    /**
     * The provider that supplies BlockStates to place.
     */
    protected final BlockStateProvider stateProvider;


    /**
     * Builds a {@link SimpleWeightedRandomList} from a list of {@link BlockState}s. Every entry
     * will have the same weight.
     *
     * @param states The states to build the list from.
     * @return The built list.
     */
    protected static SimpleWeightedRandomList<BlockState> buildWeightedList(List<BlockState> states) {
        var builder = SimpleWeightedRandomList.<BlockState>builder();
        for (BlockState s : states) builder.add(s, 1);
        return builder.build();
    }

    /**
     * Builds a {@link SimpleWeightedRandomList} from a single {@link BlockState}.
     *
     * @param state The state to build the list from.
     * @return The built list.
     */
    protected static SimpleWeightedRandomList<BlockState> buildWeightedList(BlockState state) {
        return SimpleWeightedRandomList
                .<BlockState>builder()
                .add(state, 1)
                .build();
    }

    /**
     * Creates a new instance.
     *
     * @param block The block to place.
     */
    public PlaceBlockFeatureConfig(Block block) {
        this(block.defaultBlockState());
    }

    /**
     * Creates a new instance.
     *
     * @param state The state to place.
     */
    public PlaceBlockFeatureConfig(BlockState state) {
        this(BlockStateProvider.simple(state));
    }


    /**
     * Creates a new instance.
     *
     * @param states The states to place.
     */
    public PlaceBlockFeatureConfig(List<BlockState> states) {
        this(buildWeightedList(states));
    }

    /**
     * Creates a new instance.
     *
     * @param blocks The blocks to place.
     */
    public PlaceBlockFeatureConfig(SimpleWeightedRandomList<BlockState> blocks) {
        this.stateProvider = new WeightedStateProvider(blocks);
    }

    /**
     * Creates a new instance.
     *
     * @param blocks Provider for placeable {@link BlockState}s.
     */
    public PlaceBlockFeatureConfig(BlockStateProvider blocks) {
        this.stateProvider = blocks;
    }

    /**
     * Returns a random {@link BlockState} from the {@link #stateProvider}.
     *
     * @param random The random source to use.
     * @param pos    The position to get the state for.
     * @return The random state.
     */
    public BlockState getRandomBlock(RandomSource random, BlockPos pos) {
        return this.stateProvider.getState(random, pos);
    }

    /**
     * Places a block. This will select a random state using {@link #getRandomBlock(RandomSource, BlockPos)}
     * and the call {@link #placeBlock(FeaturePlaceContext, WorldGenLevel, BlockPos, BlockState)}.
     *
     * @param ctx The context
     * @return {@code true} if the block was placed, {@code false} otherwise.
     */
    public boolean place(FeaturePlaceContext<? extends PlaceBlockFeatureConfig> ctx) {
        BlockState state = getRandomBlock(ctx.random(), ctx.origin());
        return placeBlock(ctx, ctx.level(), ctx.origin(), state);
    }


    /**
     * The actual code to run when placing the block
     *
     * @param ctx         The context
     * @param level       The world
     * @param pos         The position where a block should be placed
     * @param targetState The state to place
     * @return {@code true} if the block was placed, {@code false} otherwise.
     */
    protected abstract boolean placeBlock(
            FeaturePlaceContext<? extends PlaceBlockFeatureConfig> ctx,
            WorldGenLevel level,
            BlockPos pos,
            BlockState targetState
    );
}
