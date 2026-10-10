package de.ambertation.wover.feature.api.features;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

/**
 * Places a single block from a {@link BlockStateProvider} (<b>{@code wover:place_block}</b>).
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}: a feature carries its own configuration and
 * its own codec. The former {@code PlaceBlockFeature<FC>} / {@code PlaceBlockFeatureConfig} pair therefore
 * merged into this single abstract class — it is both the feature and the configuration. Subclasses supply
 * the codec and the actual placement rule.
 *
 * @see de.ambertation.wover.feature.api.Features#PLACE_BLOCK
 * @see PlaceFacingBlockFeature
 */
public abstract class PlaceBlockFeature implements Feature {

    /**
     * The codec fragment for the shared {@link BlockStateProvider} field.
     *
     * @param <T> the concrete feature type
     * @return the codec fragment for the {@code entries} field
     */
    protected static <T extends PlaceBlockFeature> RecordCodecBuilder<T, BlockStateProvider> blockStateCodec() {
        return BlockStateProvider.DIRECT_CODEC
                .fieldOf("entries")
                .forGetter((T o) -> o.stateProvider);
    }

    /**
     * The provider that supplies BlockStates to place.
     */
    protected final BlockStateProvider stateProvider;

    /**
     * Builds a {@link WeightedList} from a list of {@link BlockState}s. Every entry
     * will have the same weight.
     *
     * @param states The states to build the list from.
     * @return The built list.
     */
    protected static WeightedList<BlockState> buildWeightedList(List<BlockState> states) {
        var builder = WeightedList.<BlockState>builder();
        for (BlockState s : states) builder.add(s, 1);
        return builder.build();
    }

    /**
     * Builds a {@link WeightedList} from a single {@link BlockState}.
     *
     * @param state The state to build the list from.
     * @return The built list.
     */
    protected static WeightedList<BlockState> buildWeightedList(BlockState state) {
        return WeightedList
                .<BlockState>builder()
                .add(state, 1)
                .build();
    }

    /**
     * Creates a new instance.
     *
     * @param block The block to place.
     */
    public PlaceBlockFeature(Block block) {
        this(block.defaultBlockState());
    }

    /**
     * Creates a new instance.
     *
     * @param state The state to place.
     */
    public PlaceBlockFeature(BlockState state) {
        this(BlockStateProvider.of(state));
    }

    /**
     * Creates a new instance.
     *
     * @param states The states to place.
     */
    public PlaceBlockFeature(List<BlockState> states) {
        this(buildWeightedList(states));
    }

    /**
     * Creates a new instance.
     *
     * @param blocks The blocks to place.
     */
    public PlaceBlockFeature(WeightedList<BlockState> blocks) {
        this.stateProvider = new WeightedStateProvider(blocks);
    }

    /**
     * Creates a new instance.
     *
     * @param blocks Provider for placeable {@link BlockState}s.
     */
    public PlaceBlockFeature(BlockStateProvider blocks) {
        this.stateProvider = blocks;
    }

    /**
     * Returns a random {@link BlockState} from the {@link #stateProvider}.
     *
     * @param level  The world the state is requested for.
     * @param random The random source to use.
     * @param pos    The position to get the state for.
     * @return The random state.
     */
    public BlockState getRandomBlock(WorldGenLevel level, RandomSource random, BlockPos pos) {
        return this.stateProvider.getState(level, random, pos);
    }

    /**
     * Places a block. This will select a random state using
     * {@link #getRandomBlock(WorldGenLevel, RandomSource, BlockPos)}
     * and then call {@link #placeBlock(WorldGenLevel, BlockPos, BlockState)}.
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param random    the random source
     * @param pos       the position to place at
     * @return {@code true} if the block was placed, {@code false} otherwise.
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        BlockState state = getRandomBlock(level, random, pos);
        return placeBlock(level, pos, state);
    }

    /**
     * The actual code to run when placing the block
     *
     * @param level       The world
     * @param pos         The position where a block should be placed
     * @param targetState The state to place
     * @return {@code true} if the block was placed, {@code false} otherwise.
     */
    protected abstract boolean placeBlock(
            WorldGenLevel level,
            BlockPos pos,
            BlockState targetState
    );
}
