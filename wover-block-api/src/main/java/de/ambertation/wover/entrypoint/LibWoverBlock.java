package de.ambertation.wover.entrypoint;

import de.ambertation.wover.block.impl.predicate.BlockPredicatesImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.api.provider.AutoBlockRegistryTagProvider;
import de.ambertation.wover.poi.impl.PoiManagerImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverBlock implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-block", "wover");

    @Override
    public void onInitialize() {
        //make sure the Datagen will automatically include all Tags assigned to Blocks in the BlockRegistry
        WoverDataGenEntryPoint.registerAutoProvider(AutoBlockRegistryTagProvider::new);

        BlockPredicatesImpl.ensureStaticInitialization();
        PoiManagerImpl.registerAll();
    }
}