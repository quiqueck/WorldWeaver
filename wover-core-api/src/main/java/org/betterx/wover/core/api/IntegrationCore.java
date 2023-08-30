package org.betterx.wover.core.api;

import net.fabricmc.loader.api.FabricLoader;

public class IntegrationCore {
    public static boolean hasMod(String namespace) {
        return FabricLoader.getInstance()
                           .getModContainer(namespace)
                           .isPresent();
    }

    public static final boolean RUNS_TERRABLENDER = hasMod("terrablender");
    public static final boolean RUNS_NULLSCAPE = hasMod("nullscape");

    public static final ModCore MINECRAFT = ModCore.create("minecraft");
    public static final ModCore BETTER_END = ModCore.create("betterend");
    public static final ModCore BETTER_NETHER = ModCore.create("betternether");
}
