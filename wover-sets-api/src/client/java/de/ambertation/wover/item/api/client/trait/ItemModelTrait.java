package de.ambertation.wover.item.api.client.trait;

import de.ambertation.wover.block.impl.client.trait.ItemModelTraitBuilder;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitBuilder;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;

/**
 * An {@link ItemTrait} that attaches a code-driven client model factory to an item, so its item model is
 * generated automatically for every item carrying the trait via {@link #bootstrapModels}, instead of being
 * written by hand in a separate model provider override.
 */
public interface ItemModelTrait extends ItemTrait<Item, ItemModelTrait> {
    /**
     * Builds an item's client model at datagen time.
     */
    interface ModelFactory {
        /**
         * Generates the model for a single item.
         *
         * @param key       the registry key of the item
         * @param block     the item to generate a model for
         * @param generator the model generator to use
         */
        void provideItemModels(ResourceKey<Item> key, Item block, ItemModelGenerators generator);
    }

    /**
     * Builds {@link ItemModelTrait} instances.
     */
    interface Builder extends ItemTraitBuilder<Item, ItemModelTrait> {
        /**
         * @param factory generates the item's model
         * @return the new trait
         */
        ItemModelTrait with(ModelFactory factory);
    }

    /**
     * @return the factory that will generate this trait's model
     */
    ModelFactory modelFactory();

    /**
     * Generates models for every item registered under {@code modCore} that carries this trait. Called
     * automatically during model datagen; only needed directly for custom filtering.
     *
     * @param modCore   the mod whose items should be scanned
     * @param generator the model generator to use
     */
    static void bootstrapModels(
            ModCore modCore,
            ItemModelGenerators generator
    ) {
        ItemModelTraitBuilder.bootstrapModels(modCore, generator, (k, b) -> true);
    }

    /**
     * Generates models for every item registered under {@code modCore} that carries this trait and matches
     * {@code filter}.
     *
     * @param modCore   the mod whose items should be scanned
     * @param generator the model generator to use
     * @param filter    restricts which items are processed
     */
    static void bootstrapModels(
            ModCore modCore,
            ItemModelGenerators generator,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemModelTraitBuilder.bootstrapModels(modCore, generator, filter);
    }
}
