package org.betterx.wover.feature.api.features.config;

import org.betterx.wover.block.api.BlockHelper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

/**
 * Places a Block with a {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property.
 */
public class PlaceFacingBlockConfig extends PlaceBlockFeatureConfig {
    /**
     * Codec for {@link PlaceFacingBlockConfig}.
     */
    public static final Codec<PlaceFacingBlockConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    blockStateCodec(),
                    ExtraCodecs.nonEmptyList(Direction.CODEC.listOf())
                               .fieldOf("dir")
                               .orElse(List.of(Direction.NORTH))
                               .forGetter(a -> a.directions)
            ).apply(instance, PlaceFacingBlockConfig::new)
    );

    private final List<Direction> directions;

    /**
     * Creates a new {@link PlaceFacingBlockConfig}.
     *
     * @param block The block to place.
     * @param dir   The allowed directions to place the block in.
     */
    public PlaceFacingBlockConfig(Block block, List<Direction> dir) {
        this(block.defaultBlockState(), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockConfig}.
     *
     * @param state The blockstate to place.
     * @param dir   The allowed directions to place the block in.
     */
    public PlaceFacingBlockConfig(BlockState state, List<Direction> dir) {
        this(BlockStateProvider.simple(state), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockConfig}.
     *
     * @param states The blockstates to place.
     * @param dir    The allowed directions to place the block in.
     */
    public PlaceFacingBlockConfig(List<BlockState> states, List<Direction> dir) {
        this(buildWeightedList(states), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockConfig}.
     *
     * @param blocks The blockstates to place.
     * @param dir    The allowed directions to place the block in.
     */
    public PlaceFacingBlockConfig(SimpleWeightedRandomList<BlockState> blocks, List<Direction> dir) {
        this(new WeightedStateProvider(blocks), dir);
    }

    /**
     * Creates a new {@link PlaceFacingBlockConfig}.
     *
     * @param provider The blockstate provider used to determin the states.
     * @param dir      The allowed directions to place the block in.
     */
    public PlaceFacingBlockConfig(BlockStateProvider provider, List<Direction> dir) {
        super(provider);
        directions = dir;
    }

    /**
     * Places a block, and selects a fitting direction. The direction is
     * selected from the list of directions passed to the constructor.
     * every possible direction is tested using {@link BlockState#canSurvive}.
     * Blocks are only placed in air. The first fitting direction is used.
     *
     * @param ctx         The context
     * @param level       The world
     * @param pos         The position where the block should be placed
     * @param targetState The state of the block that is being placed
     * @return {@code true} if the block was placed, {@code false} otherwise
     */
    @Override
    public boolean placeBlock(
            FeaturePlaceContext<? extends PlaceBlockFeatureConfig> ctx,
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
                lookupState.canSurvive(level, testPos);
                level.setBlock(testPos, lookupState, BlockHelper.SET_SILENT);
                return true;
            }
        }

        return false;
    }
}
