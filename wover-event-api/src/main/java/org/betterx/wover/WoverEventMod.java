package org.betterx.wover;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.state.impl.WorldStateImpl;

import net.fabricmc.api.ModInitializer;

public class WoverEventMod implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-events");

    @Override
    public void onInitialize() {
        WorldStateImpl.ensureStaticallyLoaded();
    }
}