package org.betterx.wover.entrypoint;


import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverCore implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-core", "wover");

    @Override
    public void onInitialize() {
        Configs.saveConfigs();
    }
}