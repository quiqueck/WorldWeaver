package org.betterx.test.wover;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class TestModWover implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-test-mod");

    @Override
    public void onInitialize() {
        C.log.info("Hello from TestModWover!");
    }
}
