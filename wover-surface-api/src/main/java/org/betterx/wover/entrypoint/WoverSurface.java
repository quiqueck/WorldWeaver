package org.betterx.wover.entrypoint;


import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;

import net.fabricmc.api.ModInitializer;

public class WoverSurface implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-surface");

    @Override
    public void onInitialize() {
        SurfaceRuleRegistryImpl.initialize();
    }
}