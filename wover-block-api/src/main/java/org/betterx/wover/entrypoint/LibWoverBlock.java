package org.betterx.wover.entrypoint;

import org.betterx.wover.block.impl.predicate.BlockPredicatesImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.datagen.api.provider.AutoBlockRegistryTagProvider;
import org.betterx.wover.poi.impl.PoiManagerImpl;

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