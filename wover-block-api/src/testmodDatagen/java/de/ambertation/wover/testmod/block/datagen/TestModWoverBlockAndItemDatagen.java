package de.ambertation.wover.testmod.block.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBlock;

public class TestModWoverBlockAndItemDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestModelProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverBlock.C;
    }

}
