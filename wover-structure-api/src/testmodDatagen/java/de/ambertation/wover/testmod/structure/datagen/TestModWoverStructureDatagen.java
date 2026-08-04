package de.ambertation.wover.testmod.structure.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverStructure;

public class TestModWoverStructureDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addMultiProvider(StructureProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverStructure.C;
    }

}
