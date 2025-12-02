package com.betterxlib.api.registry;

import com.betterxlib.BetterXLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Central registry holder for BetterXLib.
 * <p>
 * Mods using BetterXLib should use {@link BXRegistry} for a fluent registration API.
 * This class is for internal use and library-level registrations.
 */
public final class BXRegistries {
    // Internal registries for BetterXLib itself
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, BetterXLib.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, BetterXLib.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BetterXLib.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, BetterXLib.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetterXLib.MOD_ID);

    private BXRegistries() {
        // Utility class
    }

    /**
     * Register all deferred registers to the mod event bus.
     *
     * @param modEventBus the mod event bus
     */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    /**
     * Create a ResourceLocation for BetterXLib.
     *
     * @param path the resource path
     * @return the resource location
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(BetterXLib.MOD_ID, path);
    }
}
