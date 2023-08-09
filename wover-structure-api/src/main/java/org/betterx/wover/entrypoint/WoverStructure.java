package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.structure.impl.StructureManagerImpl;
import org.betterx.wover.structure.impl.pools.StructurePoolElementTypeManagerImpl;
import org.betterx.wover.structure.impl.pools.StructurePoolManagerImpl;
import org.betterx.wover.structure.impl.sets.StructureSetManagerImpl;

import net.fabricmc.api.ModInitializer;

public class WoverStructure implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-structure", "wover");

    @Override
    public void onInitialize() {
        StructurePoolElementTypeManagerImpl.ensureStaticallyLoaded();
        StructurePoolManagerImpl.initialize();
        StructureManagerImpl.initialize();
        StructureSetManagerImpl.initialize();
    }
}