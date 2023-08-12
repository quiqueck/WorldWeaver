package org.betterx.wover.testmod.structure.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

public class WoverStructureDatagenTestMod extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(new StructureProvider());
    }

    @Override
    protected ModCore modCore() {
        return WoverStructureTestMod.C;
    }

}
