package org.betterx.wover.core.api;

import net.fabricmc.loader.api.FabricLoader;

public class IntegrationCore {
    public static boolean hasMod(String namespace) {
        return FabricLoader.getInstance()
                           .getModContainer(namespace)
                           .isPresent();
    }

    public static final boolean RUNS_TERRABLENDER = hasMod("terrablender");
}
