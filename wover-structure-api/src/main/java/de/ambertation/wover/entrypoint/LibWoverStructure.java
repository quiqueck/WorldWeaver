package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.structure.impl.StructureManagerImpl;
import de.ambertation.wover.structure.impl.pools.StructurePoolElementTypeManagerImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverStructure implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-structure", "wover");

    @Override
    public void onInitialize() {
        StructurePoolElementTypeManagerImpl.ensureStaticallyLoaded();
        //StructurePoolManagerImpl.initialize(); //done in the wover.datapack.registry entrypoint
        StructureManagerImpl.initialize();
        //StructureSetManagerImpl.initialize(); //done in the wover.datapack.registry entrypoint
    }
}