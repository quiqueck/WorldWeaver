package org.betterx.wover.entrypoint;

import org.betterx.wover.block.impl.predicate.BlockPredicatesImpl;
import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class WoverBlockAndItem implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-block", "wover");

    @Override
    public void onInitialize() {
        BlockPredicatesImpl.ensureStaticInitialization();
    }
}