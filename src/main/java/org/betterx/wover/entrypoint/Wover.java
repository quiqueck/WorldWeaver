package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class Wover implements ModInitializer {
    public static final ModCore C = ModCore.create("wover");

    @Override
    public void onInitialize() {
        C.LOG.info("Hello Fabric world!");
    }
}
