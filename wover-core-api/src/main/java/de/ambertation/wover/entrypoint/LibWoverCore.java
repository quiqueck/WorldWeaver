package de.ambertation.wover.entrypoint;


import de.ambertation.wover.config.api.Configs;
import de.ambertation.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class LibWoverCore implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-core", "wover");

    @Override
    public void onInitialize() {
        Configs.saveConfigs();
    }
}