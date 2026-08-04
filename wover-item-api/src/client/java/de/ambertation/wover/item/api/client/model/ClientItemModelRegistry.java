package de.ambertation.wover.item.api.client.model;

import de.ambertation.wover.entrypoint.LibWoverItem;
import de.ambertation.wover.item.api.model.ItemModelBinding;
import de.ambertation.wover.item.api.model.ItemModelKey;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry mapping an {@link ItemModelKey} to the {@link ClientItemModelFactory} that generates its
 * model. Populated from a {@code wover.client.traits} entrypoint; the datagen item walk resolves a binding here.
 */
@Environment(EnvType.CLIENT)
public class ClientItemModelRegistry {
    private static final Map<ItemModelKey<?>, ClientItemModelFactory<?>> FACTORIES = new HashMap<>();

    private ClientItemModelRegistry() {
    }

    /**
     * Registers the factory for an item model key.
     *
     * @param key     the item model shape
     * @param factory the generator for that shape
     * @param <P>     the payload type
     */
    public static <P> void register(ItemModelKey<P> key, ClientItemModelFactory<P> factory) {
        FACTORIES.put(key, factory);
    }

    /**
     * Resolves and runs the factory for a binding.
     *
     * @param binding   the item's model binding
     * @param key       the registry key of the item
     * @param item      the item to generate a model for
     * @param generator the model generator to use
     * @return {@code true} if a factory was found and invoked, {@code false} if the key was not registered
     */
    @SuppressWarnings("unchecked")
    public static boolean apply(
            ItemModelBinding binding,
            ResourceKey<Item> key,
            Item item,
            ItemModelGenerators generator
    ) {
        final ClientItemModelFactory<Object> factory = (ClientItemModelFactory<Object>) FACTORIES.get(binding.modelKey());
        if (factory == null) {
            LibWoverItem.C.LOG.error("No client item model factory registered for " + binding.modelKey() + " (item " + key + ")");
            return false;
        }
        factory.provide(key, item, generator, binding.payload());
        return true;
    }
}
