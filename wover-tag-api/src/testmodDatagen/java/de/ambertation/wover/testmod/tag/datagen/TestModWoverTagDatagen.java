package de.ambertation.wover.testmod.tag.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverTag;

public class TestModWoverTagDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {

    }

    @Override
    protected ModCore modCore() {
        return TestModWoverTag.C;
    }

}
