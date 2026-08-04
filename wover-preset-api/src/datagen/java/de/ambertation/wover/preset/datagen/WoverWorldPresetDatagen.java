package de.ambertation.wover.preset.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.entrypoint.LibWoverWorldPreset;

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
