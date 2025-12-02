package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

/**
 * A wrapper around a registered item that provides convenient access methods.
 *
 * @param <T> the item type
 */
public class ItemEntry<T extends Item> implements Supplier<T> {
    private final DeferredItem<T> item;
    private final ResourceLocation id;

    ItemEntry(DeferredItem<T> item, ResourceLocation id) {
        this.item = item;
        this.id = id;
    }

    @Override
    public T get() {
        return item.get();
    }

    /**
     * Get the resource location (ID) of this item.
     *
     * @return the resource location
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Get the deferred item holder.
     *
     * @return the deferred item
     */
    public DeferredItem<T> getDelegate() {
        return item;
    }

    /**
     * Create a new ItemStack of this item with count 1.
     *
     * @return a new item stack
     */
    public ItemStack stack() {
        return new ItemStack(get());
    }

    /**
     * Create a new ItemStack of this item with the specified count.
     *
     * @param count the stack size
     * @return a new item stack
     */
    public ItemStack stack(int count) {
        return new ItemStack(get(), count);
    }

    /**
     * Check if an ItemStack is of this item.
     *
     * @param stack the stack to check
     * @return true if the stack is of this item
     */
    public boolean is(ItemStack stack) {
        return stack.is(get());
    }
}
