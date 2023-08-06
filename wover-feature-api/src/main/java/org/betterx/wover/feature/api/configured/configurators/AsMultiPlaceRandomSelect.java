package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Places a random BlockStates with per BlockState placement rules ({@link RandomSelectorFeature}).
 * <p>
 * Allows you to select a random {@link BlockState} based on a weight. Each state can have a unique
 * ID (either manually specified or automatically chosen). The {@link Placer} can use this numeric ID
 * to place the feature, allowing you to have per {@link BlockState} placement logic.
 *
 * @see #placement(Placer)
 */
public interface AsMultiPlaceRandomSelect extends FeatureConfigurator<RandomFeatureConfiguration, RandomSelectorFeature> {
    /**
     * Will add all possible {@link BlockState}s of the passed {@link Block}. The weight is the chance of the
     * {@link Block} itself beeing selected. When the Block was selected, a random {@link BlockState} will be chosen.
     * All BlockStates have the same chance of being selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param block  The Block to add all states from
     * @param weight The weight of the Block
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAllStates(Block block, int weight);

    /**
     * Will add each passed Block with its default BlockState. The weight is the chance of the
     * set of Blocks beeing selected. When the Set was selected, a random Block frm this set will be chosen.
     * All Blocks have the same chance of being selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param weight   The weight of the set of Blocks
     * @param blockSet The set of Blocks to add
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAll(int weight, Block... blockSet);

    /**
     * Will add all possible {@link BlockState}s for the passed property for the passed {@link Block}. The weight is the chance of the
     * {@link Block} itself being selected. When the Block was selected, a random {@link BlockState} will be chosen.
     * All BlockStates have the same chance of being selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param prop   The property to add all states from
     * @param block  The Block to add all states from
     * @param weight The weight of the Block
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight);

    /**
     * Will add the passed {@link Block} with its default {@link BlockState}. The weight is the chance of the
     * {@link Block} beeing selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param block  The Block to add
     * @param weight The weight of the Block
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(Block block, float weight);

    /**
     * Will add the passed {@link BlockState}. The weight is the chance of the
     * {@link BlockState} beeing selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param state  The BlockState to add
     * @param weight The weight of the BlockState
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(BlockState state, float weight);

    /**
     * Will add the passed {@link BlockStateProvider}. The weight is the chance of the
     * provider being selected.
     * <p>
     * This method will automatically choose the same ID for all {@link BlockState}s.
     *
     * @param provider The BlockStateProvider to add
     * @param weight   The weight of the BlockStateProvider
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight);

    /**
     * Will add all possible {@link BlockState}s of the passed {@link Block}. The weight is the chance of the
     * {@link Block} itself beeing selected. When the Block was selected, a random {@link BlockState} will be chosen.
     * All BlockStates have the same chance of being selected.
     *
     * @param block  The Block to add all states from
     * @param weight The weight of the Block
     * @param id     The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAllStates(Block block, int weight, int id);

    /**
     * Will add each passed Block with its default BlockState. The weight is the chance of the
     * set of Blocks beeing selected. When the Set was selected, a random Block frm this set will be chosen.
     * All Blocks have the same chance of being selected.
     *
     * @param weight   The weight of the set of Blocks
     * @param id       The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @param blockSet The set of Blocks to add
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAll(int weight, int id, Block... blockSet);

    /**
     * Will add all possible {@link BlockState}s for the passed property for the passed {@link Block}. The weight is the chance of the
     * {@link Block} itself being selected. When the Block was selected, a random {@link BlockState} will be chosen.
     * All BlockStates have the same chance of being selected.
     *
     * @param prop   The property to add all states from
     * @param block  The Block to add all states from
     * @param weight The weight of the Block
     * @param id     The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @return the same instance
     */
    AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight, int id);

    /**
     * Will add the passed {@link Block} with its default {@link BlockState}. The weight is the chance of the
     * {@link Block} beeing selected.
     *
     * @param block  The Block to add
     * @param weight The weight of the Block
     * @param id     The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(Block block, float weight, int id);

    /**
     * Will add the passed {@link BlockState}. The weight is the chance of the
     * {@link BlockState} beeing selected.
     *
     * @param state  The BlockState to add
     * @param weight The weight of the BlockState
     * @param id     The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(BlockState state, float weight, int id);

    /**
     * Will add the passed {@link BlockStateProvider}. The weight is the chance of the
     * provider being selected.
     *
     * @param provider The BlockStateProvider to add
     * @param weight   The weight of the BlockStateProvider
     * @param id       The ID that will be passed to the {@link Placer} selected with {@link #placement(Placer)}
     * @return the same instance
     */
    AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight, int id);

    /**
     * Defines the {@link Placer} that is being used.
     * <p>
     * The builder determines which placement rules are selected depending on a numeric ID. IDs get assigned
     * when adding the states and blocks.
     *
     * @param placer The builder to use
     * @return the same instance
     */
    AsMultiPlaceRandomSelect placement(Placer placer);

    /**
     * Function used to determine the placement for a Block/BlockState in a {@link AsMultiPlaceRandomSelect}.
     * The builder recives a {@link FeaturePlacementBuilder} and a Numeric
     * ID, and is responsible for refining the Placement rules for all added Blocks and BlockStates assigned to
     * that id.
     */
    @FunctionalInterface
    interface Placer {
        /**
         * Called when the Feature is being placed.
         *
         * @param builder The FeaturePlacementBuilder
         * @param id      The ID for the Blocks and BlockStates that need to get placed
         * @return A {@link Holder} containing the {@link PlacedFeature} and the {@link FeaturePlacementBuilder}
         */
        Holder<PlacedFeature> place(
                FeaturePlacementBuilder builder,
                int id
        );
    }
}
