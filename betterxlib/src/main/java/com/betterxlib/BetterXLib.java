package com.betterxlib;

import com.betterxlib.api.registry.BXRegistries;
import com.betterxlib.impl.biome.BiomeModifierRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BetterXLib - A unified library mod for BetterEnd and BetterNether ports to NeoForge.
 * <p>
 * This library provides essential functionality including:
 * <ul>
 *     <li>Registry utilities with fluent API</li>
 *     <li>Base block and item classes with built-in functionality</li>
 *     <li>Biome API for End and Nether dimensions</li>
 *     <li>World generation utilities (features, structures, surface rules)</li>
 *     <li>Emissive texture rendering system</li>
 *     <li>Math utilities including noise and SDF functions</li>
 * </ul>
 */
@Mod(BetterXLib.MOD_ID)
public class BetterXLib {
    public static final String MOD_ID = "betterxlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public BetterXLib(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("BetterXLib initializing...");

        // Register deferred registers
        BXRegistries.register(modEventBus);
        BiomeModifierRegistry.register(modEventBus);

        // Register lifecycle event handlers
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register NeoForge event bus listeners
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);

        // Register configuration
        modContainer.registerConfig(ModConfig.Type.COMMON, BXConfig.SPEC);

        LOGGER.info("BetterXLib initialized successfully");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.debug("BetterXLib common setup complete");
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.debug("BetterXLib client setup complete");
        });
    }

    private void onServerStarting(ServerStartingEvent event) {
        LOGGER.debug("BetterXLib server starting");
    }
}
