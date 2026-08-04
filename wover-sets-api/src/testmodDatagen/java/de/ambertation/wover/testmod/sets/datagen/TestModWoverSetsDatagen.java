package de.ambertation.wover.testmod.sets.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSets;

public class TestModWoverSetsDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestSetProvider::new);
        globalPack.addProvider(TestModelProvider::new);
        globalPack.addProvider(TestLootProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverSets.C;
    }

}
