package de.ambertation.wover.feature.api.features;

import de.ambertation.wover.block.api.BlockHelper;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

/**
 * Places a Block with a {@link HorizontalDirectionalBlock#FACING} property
 * (<b>{@code wover:place_block}</b>).
 * <p>
 * This is the concrete feature registered as {@code wover:place_block}. It replaces the 26.1
 * {@code PlaceFacingBlockConfig}, which was a {@code FeatureConfiguration} rather than a {@link Feature} —
 * 26.3 merged the two concepts. Serialized field names ({@code entries}, {@code dir}) are unchanged.
 */
public class PlaceFacingBlockFeature extends PlaceBlockFeature {
    /**
     * Codec for {@link PlaceFacingBlockFeature}.
     */
    public static final MapCodec<PlaceFacingBlockFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    blockStateCodec(),
                    ExtraCodecs.nonEmptyList(Direction.CODEC.listOf())
                               .fieldOf("dir")
                               .orElse(List.of(Direction.NORTH))
                               .forGetter(a -> a.directions)
            ).apply(instance, PlaceFacingBlockFeature::new)
    );

    private final List<Direction> directions;

    /**
     * Creates a new {@link PlaceFacingBlockFeature}.
     *
     * @param block The block to place.
     * @param dir   The allowed directions to place the block in.
     */
    public PlaceFacingBlockFeature(Block block, List<Direction> dir) {
        this(block.defaultBlockState(), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockFeature}.
     *
     * @param state The blockstate to place.
     * @param dir   The allowed directions to place the block in.
     */
    public PlaceFacingBlockFeature(BlockState state, List<Direction> dir) {
        this(BlockStateProvider.of(state), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockFeature}.
     *
     * @param states The blockstates to place.
     * @param dir    The allowed directions to place the block in.
     */
    public PlaceFacingBlockFeature(List<BlockState> states, List<Direction> dir) {
        this(buildWeightedList(states), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockFeature}.
     *
     * @param blocks The blockstates to place.
     * @param dir    The allowed directions to place the block in.
     */
    public PlaceFacingBlockFeature(WeightedList<BlockState> blocks, List<Direction> dir) {
        this(new WeightedStateProvider(blocks), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockFeature}.
     *
     * @param provider The blockstate provider used to determin the states.
     * @param dir      The allowed directions to place the block in.
     */
    public PlaceFacingBlockFeature(BlockStateProvider provider, List<Direction> dir) {
        super(provider);
        directions = dir;
    }

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<PlaceFacingBlockFeature> codec() {
        return CODEC;
    }

    /**
     * Places a block, and selects a fitting direction. The direction is
     * selected from the list of directions passed to the constructor.
     * every possible direction is tested using {@link BlockState#canSurvive}.
     * Blocks are only placed in air. The first fitting direction is used.
     *
     * @param level       The world
     * @param pos         The position where the block should be placed
     * @param targetState The state of the block that is being placed
     * @return {@code true} if the block was placed, {@code false} otherwise
     */
    @Override
    public boolean placeBlock(
            WorldGenLevel level,
            BlockPos pos,
            BlockState targetState
    ) {
        BlockState lookupState;
        BlockPos testPos;
        for (Direction dir : directions) {
            testPos = pos.relative(dir);
            lookupState = targetState.setValue(HorizontalDirectionalBlock.FACING, dir);
            if (level.getBlockState(testPos).isAir() && lookupState.canSurvive(level, testPos)) {
                level.setBlock(testPos, lookupState, BlockHelper.SET_SILENT);
                return true;
            }
        }

        return false;
    }
}
