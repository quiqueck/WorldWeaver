package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * A {@link FeatureConfigurator} that allows to add blocks with a weight.
 *
 * @param <FC> The {@link FeatureConfiguration} of the {@link Feature} that is configured
 * @param <F>  The {@link Feature} that is configured
 * @param <W>  The type of the {@link FeatureConfigurator} that is returned
 */
public interface BaseWeightedBlock<FC extends FeatureConfiguration, F extends Feature<FC>, W extends BaseWeightedBlock<FC, F, W>> extends FeatureConfigurator<FC, F> {
    /**
     * Allows the Block in its default BlockState to be placed. The Block must have a
     * {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property.
     *
     * @param block  The Block to place
     * @param weight The weight of the Block
     * @return the same instance
     */
    W add(Block block, int weight);

    /**
     * Allows the BlockState to be placed. The BlockState must have a
     * {@link net.minecraft.world.level.block.HorizontalDirectionalBlock#FACING} property.
     *
     * @param state  The BlockState to place
     * @param weight The weight of the BlockState
     * @return the same instance
     */
    W add(BlockState state, int weight);

    /**
     * Adds all BlockStates of the given Block to the list of possible Blocks to place.
     * <p>
     * The weight is distributed between the possible states. So if a Block has 4 states
     * and the weight is 1, each state will have a weight of 0.25.
     *
     * @param block  The Block to add all BlockStates from
     * @param weight The sum of the weights for all generated states (1 in the above example)
     * @return the same instance
     */
    W addAllStates(Block block, int weight);

    /**
     * Add the Block with all possible values for the passed IntegerProperty to the Configurator.
     * <p>
     * The weight is distributed between the possible states. So if a Block has 4 states
     * and the weight is 1, each state will have a weight of 0.25.
     *
     * @param prop   The IntegerProperty to iterate over
     * @param block  The Block to add
     * @param weight The sum of the weights for all generated states (1 in the above example)
     * @return the same instance
     */
    W addAllStatesFor(IntegerProperty prop, Block block, int weight);
}
