package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

/**
 * Places Blocks in the typical nether vegetation distribution ({@link NetherForestVegetationFeature}).
 * <p>
 * The Feature will randomly select a block from the given list of blocks and place/spread it according
 * to the default nether vegetation distribution.
 * <p>
 * You may either configur your feature using manually added BlockStates or by providing
 * a {@link WeightedStateProvider} that will be used to select the BlockState. You can not
 * use both for the same Configurator.
 */
public interface NetherForrestVegetation extends BaseWeightedBlock<NetherForestVegetationConfig, NetherForestVegetationFeature, NetherForrestVegetation> {
    /**
     * The maximum spread radius for the Blocks (how wide the x-z plane is)
     *
     * @param width the radius
     * @return the same instance
     */
    NetherForrestVegetation spreadWidth(int width);
    /**
     * The maximum spread height for the Blocks (how high the y axis is)
     *
     * @param height the height
     * @return the same instance
     */
    NetherForrestVegetation spreadHeight(int height);

    /**
     * Allows you to provide a {@link WeightedStateProvider} that will be used to select the BlockState
     * and weights.
     * <p>
     * You can not use this method if you did already add BlockStates manually using one of
     * the "add"-methods. "Add"-Methods will create automatically create
     * a {@link WeightedStateProvider} from a {@link net.minecraft.util.random.SimpleWeightedRandomList}
     * of {@link BlockState}s for you.
     *
     * @param provider The provider to use
     * @return the same instance
     */
    NetherForrestVegetation provider(WeightedStateProvider provider);
}
