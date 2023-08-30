package org.betterx.wover.testmod.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverWorldGeneratorTestMod;

public class WoverWorldGeneratorDatagenTestMod extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addMultiProvider(BiomeProvider::new);
        globalPack.addMultiProvider(FeatureProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return WoverWorldGeneratorTestMod.C;
    }

}
