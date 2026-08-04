package de.ambertation.wover.entrypoint;

import de.ambertation.wover.config.impl.CachedConfig;
import de.ambertation.wover.core.api.ModCore;

import net.fabricmc.api.ModInitializer;

public class LibWoverUi implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-ui", "wover");

    @Override
    public void onInitialize() {
        CachedConfig.ensureStaticallyLoaded();
    }
}