package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A wrapper around a registered block that provides convenient access methods.
 *
 * @param <T> the block type
 */
public class BlockEntry<T extends Block> implements Supplier<T> {
    private final DeferredBlock<T> block;
    private final ResourceLocation id;
    @Nullable
    private final DeferredItem<BlockItem> item;

    BlockEntry(DeferredBlock<T> block, ResourceLocation id, @Nullable DeferredItem<BlockItem> item) {
        this.block = block;
        this.id = id;
        this.item = item;
    }

    @Override
    public T get() {
        return block.get();
    }

    /**
     * Get the resource location (ID) of this block.
     *
     * @return the resource location
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Get the default block state.
     *
     * @return the default block state
     */
    public BlockState getDefaultState() {
        return get().defaultBlockState();
    }

    /**
     * Check if this block has an associated item.
     *
     * @return true if an item was registered
     */
    public boolean hasItem() {
        return item != null;
    }

    /**
     * Get the associated block item, if present.
     *
     * @return the block item, or null if none was registered
     */
    @Nullable
    public BlockItem getItem() {
        return item != null ? item.get() : null;
    }

    /**
     * Get the item as an Item, if present.
     *
     * @return the item, or null if none was registered
     */
    @Nullable
    public Item asItem() {
        return getItem();
    }

    /**
     * Get the deferred block holder.
     *
     * @return the deferred block
     */
    public DeferredBlock<T> getDelegate() {
        return block;
    }

    /**
     * Get the deferred item holder, if present.
     *
     * @return the deferred item, or null if none was registered
     */
    @Nullable
    public DeferredItem<BlockItem> getItemDelegate() {
        return item;
    }

    /**
     * Check if a block state is this block.
     *
     * @param state the block state to check
     * @return true if the state is of this block
     */
    public boolean is(BlockState state) {
        return state.is(get());
    }
}
