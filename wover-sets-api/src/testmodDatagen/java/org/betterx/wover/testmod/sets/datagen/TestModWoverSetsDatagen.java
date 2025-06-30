package org.betterx.wover.testmod.sets.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.TestModWoverSets;

public class TestModWoverSetsDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestSetProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverSets.C;
    }

}
