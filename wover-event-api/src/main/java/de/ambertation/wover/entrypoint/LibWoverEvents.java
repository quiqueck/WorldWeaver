package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.state.impl.WorldConfigImpl;
import de.ambertation.wover.state.impl.WorldDatapackConfigImpl;
import de.ambertation.wover.state.impl.WorldStateImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverEvents implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-events", "wover");

    @Override
    public void onInitialize() {
        WorldConfigImpl.initialize();
        WorldDatapackConfigImpl.initialize();
        WorldStateImpl.ensureStaticallyLoaded();
    }
}