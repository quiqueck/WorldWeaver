package org.betterx.wover.testmod.block.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverBlockAndItemTestMod;

public class WoverBlockAndItemDatagenTestMod extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {

    }

    @Override
    protected ModCore modCore() {
        return WoverBlockAndItemTestMod.C;
    }

}
