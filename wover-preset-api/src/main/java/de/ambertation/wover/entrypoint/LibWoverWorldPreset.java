package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.preset.impl.WorldPresetInfoRegistryImpl;
import de.ambertation.wover.preset.impl.WorldPresetsManagerImpl;
import de.ambertation.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverWorldPreset implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-preset", "wover");

    @Override
    public void onInitialize() {
        WorldPresetInfoRegistryImpl.initialize();
        WorldPresetsManagerImpl.initialize();
        FlatLevelPresetManagerImpl.initialize();
    }
}