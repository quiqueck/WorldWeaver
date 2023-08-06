package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Builds a column of defined block states ({@link BlockColumnFeature}).
 * <p>
 * This feature will stack {@link BlockState}s ontop of each other in  a single column. The Blocks are defined
 * for a certain direction (default: {@link Direction#UP}). The first BlockState in the list will be placed at the
 * start of the colum, all other BlockStates will be placed along the specified direction.
 * <p>
 * You can define the height of each segement. There are also some convenience Methods for
 * certain Blocktypes like {@link #addTripleShape(BlockState, IntProvider)}.
 * <p>
 * The vanilla game uses this type of feature to generate Sugar Cane, Cactus and Bamboo.
 */
public interface AsBlockColumn extends FeatureConfigurator<BlockColumnConfiguration, BlockColumnFeature> {
    /**
     * Adds a new segment with a fixed height and {@link Block} to the column.
     *
     * @param height height of the segment
     * @param block  block to place
     * @return the same instance
     */
    AsBlockColumn add(int height, Block block);
    /**
     * Adds a new segment with a fixed height and {@link BlockState} to the column.
     *
     * @param height height of the segment
     * @param state  blockstate to set
     * @return the same instance
     */
    AsBlockColumn add(int height, BlockState state);

    /**
     * Adds a new segment with a fixed height to the column. The {@link BlockState} to set is provided by the
     * given {@link BlockStateProvider}.
     *
     * @param height height of the segment
     * @param state  the blockstate provider
     * @return the same instance
     */
    AsBlockColumn add(int height, BlockStateProvider state);

    /**
     * Adds a new segment with a fixed height to the column. The {@link BlockState} to set is choosen
     * randomly chosen for every position from the given {@link BlockState}s.
     *
     * @param height height of the segment
     * @param states the blockstates to choose from
     * @return the same instance
     */
    AsBlockColumn addRandom(int height, BlockState... states);
    /**
     * Adds a new segment to the column. The {@link BlockState} to set is choosen
     * randomly chosen for every position from the given {@link BlockState}s.
     *
     * @param height a height provider that will determine the height of the segment
     * @param states the blockstates to choose from
     * @return the same instance
     */
    AsBlockColumn addRandom(IntProvider height, BlockState... states);

    /**
     * Adds a new segment using the given {@link Block} to the column.
     *
     * @param height a height provider that will determine the height of the segment
     * @param block  block to place
     * @return the same instance
     */
    AsBlockColumn add(IntProvider height, Block block);

    /**
     * Adds a new segment using the given {@link BlockState} to the column.
     *
     * @param height a height provider that will determine the height of the segment
     * @param state  blockstate to set
     * @return the same instance
     */
    AsBlockColumn add(IntProvider height, BlockState state);

    /**
     * Adds a new segment using the given {@link BlockStateProvider} to the column.
     *
     * @param height a height provider that will determine the height of the segment
     * @param state  the blockstate provider
     * @return the same instance
     */
    AsBlockColumn add(IntProvider height, BlockStateProvider state);

    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#TRIPLE_SHAPE} property.
     * <p>
     * The bottom state will be placed first, and both the top and bottom state will
     * have a height of 1.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM))
     *  .add(midHeight, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE))
     *  .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP));</pre>
     *
     * @param state     the base blockstate. This {@link BlockState} must have a
     *                  {@link org.betterx.wover.block.api.BlockProperties#TRIPLE_SHAPE} property
     * @param midHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addTripleShape(BlockState state, IntProvider midHeight);

    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#TRIPLE_SHAPE} property.
     * <p>
     * The top state will be placed first, and both the top and bottom state will
     * have a height of 1.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP))
     *  .add(midHeight, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE))
     *  .add(1, state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM));</pre>
     *
     * @param state     the base blockstate. This {@link BlockState} must have a
     *                  {@link org.betterx.wover.block.api.BlockProperties#TRIPLE_SHAPE} property
     * @param midHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addTripleShapeUpsideDown(BlockState state, IntProvider midHeight);

    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#BOTTOM} property.
     * <p>
     * The first placed block will have the bottom state set to false, and use the height provided by
     * {@code bottomHeight}. The topmost block will have the bottom state set to true, and a height of 1.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(bottomHeight, state.setValue(BlockProperties.BOTTOM, false))
     *  .add(1, state.setValue(BlockProperties.BOTTOM, true))</pre>
     *
     * @param state        the base blockstate. This {@link BlockState} must have a
     *                     {@link org.betterx.wover.block.api.BlockProperties#BOTTOM} property
     * @param bottomHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addBottomShapeUpsideDown(BlockState state, IntProvider bottomHeight);
    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#BOTTOM} property.
     * <p>
     * The first placed block will have the bottom state set to true, and a height of 1. The
     * topmost block will have the bottom state set to false, and use the height provided by
     * {@code topHeight}.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(1, state.setValue(BlockProperties.BOTTOM, true))
     *  .add(topHeight, state.setValue(BlockProperties.BOTTOM, false))</pre>
     *
     * @param state     the base blockstate. This {@link BlockState} must have a
     *                  {@link org.betterx.wover.block.api.BlockProperties#BOTTOM} property
     * @param topHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addBottomShape(BlockState state, IntProvider topHeight);
    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#TOP} property.
     * <p>
     * The first placed block will have the top state set to true, and a height of 1. The
     * topmost block will have the top state set to false, and use the height provided by
     * {@code topHeight}.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(1, state.setValue(BlockProperties.TOP, true))
     *  .add(topHeight, state.setValue(BlockProperties.TOP, false))</pre>
     *
     * @param state     the base blockstate. This {@link BlockState} must have a
     *                  {@link org.betterx.wover.block.api.BlockProperties#TOP} property
     * @param topHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addTopShapeUpsideDown(BlockState state, IntProvider topHeight);

    /**
     * Builds a column from a block that provides a
     * {@link org.betterx.wover.block.api.BlockProperties#TOP} property.
     * <p>
     * The first placed block will have the top state set to false, and use the height provided by
     * {@code bottomHeight}. The topmost block will have the top state set to true, and a height of 1.
     * <p>
     * This is a convenience method for:
     * <pre class="java">this
     *  .add(bottomHeight, state.setValue(BlockProperties.TOP, false))
     *  .add(1, state.setValue(BlockProperties.TOP, true))</pre>
     *
     * @param state        the base blockstate. This {@link BlockState} must have a
     *                     {@link org.betterx.wover.block.api.BlockProperties#TOP} property
     * @param bottomHeight The height of the middle segment
     * @return the same instance
     */
    AsBlockColumn addTopShape(BlockState state, IntProvider bottomHeight);

    /**
     * Sets the direction in which the column will be generated.
     *
     * @param dir the direction
     * @return the same instance
     */
    AsBlockColumn direction(Direction dir);

    /**
     * If the topmost segment can not be placed, the last segment will be shortened
     * until it can be placed. This ensures that the last layer is always placed.
     *
     * @return the same instance
     */
    AsBlockColumn prioritizeTip();
    /**
     * Wehether or not to prioritize the tip of the column. {@see #prioritizeTip()}
     *
     * @param prio {@code true} to prioritize the tip
     * @return the same instance
     */
    AsBlockColumn prioritizeTip(boolean prio);
    /**
     * Sets the {@link BlockPredicate} that will be used to determine if a block from the column
     * can be placed.
     *
     * @param predicate the predicate
     * @return the same instance
     */
    AsBlockColumn allowedPlacement(BlockPredicate predicate);
}
