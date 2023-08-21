package org.betterx.wover.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.entrypoint.WoverWorldGenerator;

public class WoverWorldGeneratorDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(NoiseGeneratorSettingsProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return WoverWorldGenerator.C;
    }

}
