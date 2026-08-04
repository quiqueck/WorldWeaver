package de.ambertation.wover.testmod.item.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverItem;

public class TestModWoverItemDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestEnchantmentProvider::new);
        globalPack.addProvider(BlockTagProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverItem.C;
    }

}
