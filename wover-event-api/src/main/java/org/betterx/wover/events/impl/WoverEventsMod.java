package org.betterx.wover.events.impl;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverEventsMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final ModCore C = ModCore.create("wover-events");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        System.out.println("Loaded Tag API");
        C.LOG.info("Hello better-tag-api!");
    }
}