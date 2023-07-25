package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverBlockAndItemTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-block-testmod");

    @Override
    public void onInitialize() {

    }
}