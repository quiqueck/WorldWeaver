package de.ambertation.wover.item.api.client.model;

import de.ambertation.wover.item.api.model.ItemModelKey;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The client-side generator behind an {@link ItemModelKey}: produces an item's model from a common-safe payload
 * at datagen time. Registered against its key in {@code ClientItemModelRegistry}.
 *
 * @param <P> the payload type of the {@link ItemModelKey} this factory is registered for
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientItemModelFactory<P> {
    /**
     * Generates the model for a single item.
     *
     * @param key       the registry key of the item
     * @param item      the item to generate a model for
     * @param generator the model generator to use
     * @param payload   the common-safe payload stored on the item's binding
     */
    void provide(ResourceKey<Item> key, Item item, ItemModelGenerators generator, P payload);
}
