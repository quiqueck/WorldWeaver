package com.betterxlib.api.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Fluent registration API for mods using BetterXLib.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class MyMod {
 *     public static final BXRegistry REGISTRY = BXRegistry.create("mymod");
 *
 *     public static final BlockEntry<MyBlock> MY_BLOCK = REGISTRY.block("my_block", MyBlock::new)
 *         .properties(p -> p.strength(2.0f))
 *         .withItem()
 *         .register();
 *
 *     public static void init(IEventBus bus) {
 *         REGISTRY.register(bus);
 *     }
 * }
 * }</pre>
 */
public class BXRegistry {
    private final String modId;
    private final DeferredRegister<Block> blocks;
    private final DeferredRegister<Item> items;
    private final DeferredRegister<BlockEntityType<?>> blockEntities;
    private final DeferredRegister<EntityType<?>> entities;

    private final List<BlockEntry<?>> registeredBlocks = new ArrayList<>();
    private final List<ItemEntry<?>> registeredItems = new ArrayList<>();

    private BXRegistry(String modId) {
        this.modId = modId;
        this.blocks = DeferredRegister.create(Registries.BLOCK, modId);
        this.items = DeferredRegister.create(Registries.ITEM, modId);
        this.blockEntities = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
        this.entities = DeferredRegister.create(Registries.ENTITY_TYPE, modId);
    }

    /**
     * Create a new registry for a mod.
     *
     * @param modId the mod ID
     * @return a new BXRegistry instance
     */
    public static BXRegistry create(String modId) {
        return new BXRegistry(modId);
    }

    /**
     * Register all deferred registers to the mod event bus.
     *
     * @param modEventBus the mod event bus
     */
    public void register(IEventBus modEventBus) {
        blocks.register(modEventBus);
        items.register(modEventBus);
        blockEntities.register(modEventBus);
        entities.register(modEventBus);
    }

    /**
     * Get the mod ID for this registry.
     *
     * @return the mod ID
     */
    public String getModId() {
        return modId;
    }

    /**
     * Create a ResourceLocation for this mod.
     *
     * @param path the resource path
     * @return the resource location
     */
    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    /**
     * Start building a block registration.
     *
     * @param name the block name
     * @param blockSupplier supplier for the block instance
     * @param <T> the block type
     * @return a block builder
     */
    public <T extends Block> BlockBuilder<T> block(String name, Supplier<T> blockSupplier) {
        return new BlockBuilder<>(this, name, blockSupplier);
    }

    /**
     * Start building an item registration.
     *
     * @param name the item name
     * @param itemSupplier supplier for the item instance
     * @param <T> the item type
     * @return an item builder
     */
    public <T extends Item> ItemBuilder<T> item(String name, Supplier<T> itemSupplier) {
        return new ItemBuilder<>(this, name, itemSupplier);
    }

    /**
     * Start building an entity registration.
     *
     * @param name the entity name
     * @param typeSupplier supplier for the entity type
     * @param <T> the entity type
     * @return an entity builder
     */
    public <T extends Entity> EntityBuilder<T> entity(String name, Supplier<EntityType<T>> typeSupplier) {
        return new EntityBuilder<>(this, name, typeSupplier);
    }

    /**
     * Start building a block entity registration.
     *
     * @param name the block entity name
     * @param typeSupplier supplier for the block entity type
     * @param <T> the block entity type
     * @return a block entity builder
     */
    public <T extends BlockEntity> BlockEntityBuilder<T> blockEntity(String name, Supplier<BlockEntityType<T>> typeSupplier) {
        return new BlockEntityBuilder<>(this, name, typeSupplier);
    }

    // Internal accessors
    DeferredRegister<Block> getBlocks() {
        return blocks;
    }

    DeferredRegister<Item> getItems() {
        return items;
    }

    DeferredRegister<BlockEntityType<?>> getBlockEntities() {
        return blockEntities;
    }

    DeferredRegister<EntityType<?>> getEntities() {
        return entities;
    }

    void addBlock(BlockEntry<?> entry) {
        registeredBlocks.add(entry);
    }

    void addItem(ItemEntry<?> entry) {
        registeredItems.add(entry);
    }

    /**
     * Get all registered blocks.
     *
     * @return an unmodifiable list of registered blocks
     */
    public List<BlockEntry<?>> getRegisteredBlocks() {
        return Collections.unmodifiableList(registeredBlocks);
    }

    /**
     * Get all registered items.
     *
     * @return an unmodifiable list of registered items
     */
    public List<ItemEntry<?>> getRegisteredItems() {
        return Collections.unmodifiableList(registeredItems);
    }
}
