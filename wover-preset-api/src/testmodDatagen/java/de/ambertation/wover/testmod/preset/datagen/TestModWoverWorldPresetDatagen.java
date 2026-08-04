package de.ambertation.wover.testmod.preset.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverWorldPreset;

public class TestModWoverWorldPresetDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(PresetProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverWorldPreset.C;
    }

}
