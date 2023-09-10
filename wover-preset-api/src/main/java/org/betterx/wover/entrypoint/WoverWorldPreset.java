package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.preset.impl.WorldPresetInfoRegistryImpl;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;
import org.betterx.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.fabricmc.api.ModInitializer;

public class WoverWorldPreset implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-preset", "wover");

    @Override
    public void onInitialize() {
        WorldPresetInfoRegistryImpl.initialize();
        WorldPresetsManagerImpl.initialize();
        FlatLevelPresetManagerImpl.initialize();
    }
}