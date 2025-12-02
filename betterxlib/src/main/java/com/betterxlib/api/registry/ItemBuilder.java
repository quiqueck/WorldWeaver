package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Builder for registering items with a fluent API.
 *
 * @param <T> the item type
 */
public class ItemBuilder<T extends Item> {
    private final BXRegistry registry;
    private final String name;
    private final Supplier<T> itemSupplier;

    private final List<TagKey<Item>> tags = new ArrayList<>();
    @Nullable
    private ResourceLocation creativeTab = null;

    ItemBuilder(BXRegistry registry, String name, Supplier<T> itemSupplier) {
        this.registry = registry;
        this.name = name;
        this.itemSupplier = itemSupplier;
    }

    /**
     * Add tags to this item.
     *
     * @param itemTags the tags to add
     * @return this builder
     */
    @SafeVarargs
    public final ItemBuilder<T> tags(TagKey<Item>... itemTags) {
        this.tags.addAll(List.of(itemTags));
        return this;
    }

    /**
     * Add this item to a creative mode tab.
     *
     * @param tab the creative mode tab resource location
     * @return this builder
     */
    public ItemBuilder<T> creativeTab(ResourceLocation tab) {
        this.creativeTab = tab;
        return this;
    }

    /**
     * Complete the registration and return the ItemEntry.
     *
     * @return the registered item entry
     */
    public ItemEntry<T> register() {
        ResourceLocation id = registry.id(name);
        DeferredItem<T> deferredItem = registry.getItems().register(name, itemSupplier);

        ItemEntry<T> entry = new ItemEntry<>(deferredItem, id);
        registry.addItem(entry);
        return entry;
    }
}
