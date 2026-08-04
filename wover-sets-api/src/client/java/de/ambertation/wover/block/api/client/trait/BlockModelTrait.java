package de.ambertation.wover.block.api.client.trait;

import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitBuilder;
import de.ambertation.wover.block.impl.client.trait.BlockModelTraitBuilder;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;

/**
 * A {@link BlockTrait} that attaches a code-driven client model factory to a block, so its blockstate/model
 * (and any item model) is generated automatically for every block carrying the trait via
 * {@link #bootstrapModels}, instead of being written by hand in a separate
 * {@link de.ambertation.wover.datagen.api.provider.WoverModelProvider} override. This is the client-side counterpart of
 * {@link de.ambertation.wover.block.api.trait.behaviour.LootTableTrait}/
 * {@link de.ambertation.wover.block.api.trait.BlockRecipeTrait}.
 */
public interface BlockModelTrait extends BlockTrait<Block, BlockModelTrait> {
    /**
     * Builds a block's client model(s) at datagen time.
     */
    interface ModelFactory {
        /**
         * Generates the blockstate/model (and any item model) for a single block.
         *
         * @param key       the registry key of the block
         * @param block     the block to generate models for
         * @param generator the model generator to use
         */
        void provideBlockModels(ResourceKey<Block> key, Block block, WoverBlockModelGenerators generator);
    }

    /**
     * Builds {@link BlockModelTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, BlockModelTrait> {
        /**
         * @param factory generates the block's model(s)
         * @return the new trait
         */
        BlockModelTrait with(ModelFactory factory);
    }

    /**
     * @return the factory that will generate this trait's model(s)
     */
    ModelFactory modelFactory();

    /**
     * Generates models for every block registered under {@code modCore} that carries this trait. Called
     * automatically during model datagen; only needed directly for custom filtering.
     *
     * @param modCore   the mod whose blocks should be scanned
     * @param generator the model generator to use
     */
    static void bootstrapModels(
            ModCore modCore,
            WoverBlockModelGenerators generator
    ) {
        BlockModelTraitBuilder.bootstrapModels(modCore, generator, (k, b) -> true);
    }

    /**
     * Generates models for every block registered under {@code modCore} that carries this trait and matches
     * {@code filter}.
     *
     * @param modCore   the mod whose blocks should be scanned
     * @param generator the model generator to use
     * @param filter    restricts which blocks are processed
     */
    static void bootstrapModels(
            ModCore modCore,
            WoverBlockModelGenerators generator,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockModelTraitBuilder.bootstrapModels(modCore, generator, filter);
    }
}
