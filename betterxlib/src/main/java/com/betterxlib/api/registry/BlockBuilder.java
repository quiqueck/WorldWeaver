package com.betterxlib.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Builder for registering blocks with a fluent API.
 *
 * @param <T> the block type
 */
public class BlockBuilder<T extends Block> {
    private final BXRegistry registry;
    private final String name;
    private final Supplier<T> blockSupplier;

    private boolean withItem = false;
    private UnaryOperator<Item.Properties> itemPropertiesModifier = UnaryOperator.identity();
    private Function<T, BlockItem> blockItemFactory = null;
    private final List<TagKey<Block>> blockTags = new ArrayList<>();
    private final List<TagKey<Item>> itemTags = new ArrayList<>();
    @Nullable
    private ResourceLocation creativeTab = null;

    BlockBuilder(BXRegistry registry, String name, Supplier<T> blockSupplier) {
        this.registry = registry;
        this.name = name;
        this.blockSupplier = blockSupplier;
    }

    /**
     * Register a BlockItem for this block with default properties.
     *
     * @return this builder
     */
    public BlockBuilder<T> withItem() {
        this.withItem = true;
        return this;
    }

    /**
     * Register a BlockItem for this block with custom properties.
     *
     * @param propertiesModifier modifier for item properties
     * @return this builder
     */
    public BlockBuilder<T> withItem(UnaryOperator<Item.Properties> propertiesModifier) {
        this.withItem = true;
        this.itemPropertiesModifier = propertiesModifier;
        return this;
    }

    /**
     * Register a custom BlockItem for this block.
     *
     * @param factory factory to create the block item
     * @return this builder
     */
    public BlockBuilder<T> withItem(Function<T, BlockItem> factory) {
        this.withItem = true;
        this.blockItemFactory = factory;
        return this;
    }

    /**
     * Add block tags to this block.
     *
     * @param tags the tags to add
     * @return this builder
     */
    @SafeVarargs
    public final BlockBuilder<T> blockTags(TagKey<Block>... tags) {
        this.blockTags.addAll(List.of(tags));
        return this;
    }

    /**
     * Add item tags to the block item.
     *
     * @param tags the tags to add
     * @return this builder
     */
    @SafeVarargs
    public final BlockBuilder<T> itemTags(TagKey<Item>... tags) {
        this.itemTags.addAll(List.of(tags));
        return this;
    }

    /**
     * Add this block to a creative mode tab.
     *
     * @param tab the creative mode tab resource location
     * @return this builder
     */
    public BlockBuilder<T> creativeTab(ResourceLocation tab) {
        this.creativeTab = tab;
        return this;
    }

    /**
     * Complete the registration and return the BlockEntry.
     *
     * @return the registered block entry
     */
    public BlockEntry<T> register() {
        ResourceLocation id = registry.id(name);

        // Register the block
        DeferredBlock<T> deferredBlock = registry.getBlocks().register(name, blockSupplier);

        // Optionally register the item
        DeferredItem<BlockItem> deferredItem = null;
        if (withItem) {
            if (blockItemFactory != null) {
                deferredItem = registry.getItems().register(name, () -> blockItemFactory.apply(deferredBlock.get()));
            } else {
                deferredItem = registry.getItems().register(name, () -> {
                    Item.Properties props = itemPropertiesModifier.apply(new Item.Properties());
                    return new BlockItem(deferredBlock.get(), props);
                });
            }
        }

        BlockEntry<T> entry = new BlockEntry<>(deferredBlock, id, deferredItem);
        registry.addBlock(entry);
        return entry;
    }

    /**
     * Helper to create block properties with common settings.
     *
     * @param copyFrom block to copy properties from
     * @return block properties
     */
    public static BlockBehaviour.Properties propertiesFrom(Block copyFrom) {
        return BlockBehaviour.Properties.ofFullCopy(copyFrom);
    }
}
