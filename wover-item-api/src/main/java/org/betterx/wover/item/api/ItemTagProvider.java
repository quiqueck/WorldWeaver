package org.betterx.wover.item.api;

import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.resources.ResourceLocation;

/**
 * If an Item Object implements this interface and is registered with the ItemRegistry, #registerItemTags will be called
 * during the tag bootstrap datagen process.
 */
@Deprecated(forRemoval = true)
public interface ItemTagProvider {
    /**
     * Register item tags for the given location.
     *
     * @param location The location of the item as it was registered with then ItemRegistry
     * @param context  The context to add tags to
     */
    void registerItemTags(ResourceLocation location, ItemTagBootstrapContext context);
}
