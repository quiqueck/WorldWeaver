package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.fabricmc.api.ModInitializer;

public class WoverWorldPreset implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-preset", "wover");

    @Override
    public void onInitialize() {
        WorldPresetsManagerImpl.initialize();
    }
}