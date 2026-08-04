package de.ambertation.wover.item.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.entrypoint.LibWoverItem;

public class LibWoverItemDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {

    }

    @Override
    protected ModCore modCore() {
        return LibWoverItem.C;
    }

}
