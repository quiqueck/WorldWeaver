package org.betterx.wover.entrypoint;


import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverCore implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-core");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        System.out.println("Loaded Tag API:" + C.id("foo"));
        C.LOG.info("Hello better-tag-api!");
    }
}