package org.betterx.wover{postfix}.entrypoint;

import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class {mainClass} implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-{namespace}");

    @Override
    public void onInitialize() {

    }
}