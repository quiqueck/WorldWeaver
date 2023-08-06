package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.features.PlaceBlockFeature;
import org.betterx.wover.feature.api.features.config.PlaceFacingBlockConfig;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Places blocks that have a directional state ({@link PlaceBlockFeature} with {@link PlaceFacingBlockConfig}).
 * <p>
 * This can be used to place Blocks that have a
 * {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property. You can add multiple Blocks
 * or BlockStates to this Configurator. The Feature will choose one of them randomly by weight.
 */
public interface FacingBlock extends BaseWeightedBlock<PlaceFacingBlockConfig, PlaceBlockFeature<PlaceFacingBlockConfig>, FacingBlock> {
    /**
     * Allows the Block to be placed in all Horizontal directions
     *
     * @return the same instance
     */
    FacingBlock allHorizontal();
    /**
     * Allows the Block to be placed in all Vertical directions
     *
     * @return the same instance
     */
    FacingBlock allVertical();

    /**
     * Allows the Block to be placed in all directions
     *
     * @return the same instance
     */
    FacingBlock allDirections();
    /**
     * Allows the Block in its default BlockState to be placed. The Block must have a
     * {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property.
     * <p>
     * This is a convenience method for {@link #add(Block, int)} with the weight set to 1
     *
     * @param block The Block to place
     * @return the same instance
     * @see #add(Block, int)
     */
    FacingBlock add(Block block);

    /**
     * Allows the BlockState to be placed. The BlockState must have a
     * {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property.
     * <p>
     * This is a convenience method for {@link #add(BlockState, int)} with the weight set to 1
     *
     * @param state The BlockState to place
     * @return the same instance
     * @see #add(BlockState, int)
     */
    FacingBlock add(BlockState state);
}
