package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.state.impl.WorldConfigImpl;
import org.betterx.wover.state.impl.WorldStateImpl;

import net.fabricmc.api.ModInitializer;

public class WoverEvents implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-events", "wover");

    @Override
    public void onInitialize() {
        WorldConfigImpl.initialize();
        WorldStateImpl.ensureStaticallyLoaded();
    }
}