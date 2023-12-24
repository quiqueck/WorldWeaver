package org.betterx.wover.preset.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.entrypoint.LibWoverWorldPreset;

public class WoverWorldPresetDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(WorldPresetInfoProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return LibWoverWorldPreset.C;
    }
}
