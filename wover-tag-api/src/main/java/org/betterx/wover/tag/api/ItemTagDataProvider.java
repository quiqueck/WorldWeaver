package org.betterx.wover.tag.api;

import org.betterx.wover.tag.api.builder.ItemTagBuilder;

/**
 * Provides item tags during DataGen.
 * <p>
 * When an Item or a Block implements this interface and is registered to {@link net.minecraft.core.registries.BuiltInRegistries#BLOCK} or
 * {@link net.minecraft.core.registries.BuiltInRegistries#ITEM} Registry, the {@code WoverDataGenEntryPoint} will automatically call
 * {@link #addItemTags(ItemTagBuilder)} to add ItemTags to the global pack.
 */
public interface ItemTagDataProvider {
    /**
     * Adds item tags to the global pack.
     * <p>
     * <b>Important:</b> This method is only called during DataGen.
     *
     * @param builder The builder you can use to configure your Tags.
     */
    void addItemTags(ItemTagBuilder builder);
}
