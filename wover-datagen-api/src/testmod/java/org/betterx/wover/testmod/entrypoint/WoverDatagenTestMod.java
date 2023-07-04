package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverDatagenTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like otehr Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-datagen-testmod");

    @Override
    public void onInitialize() {

    }
}