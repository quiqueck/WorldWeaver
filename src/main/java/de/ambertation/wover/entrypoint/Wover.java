package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.core.impl.registry.ModCoreImpl;
import de.ambertation.wover.ui.api.VersionChecker;

import net.fabricmc.api.ModInitializer;

public class Wover implements ModInitializer {
    public static final ModCore C = ModCoreImpl.GLOBAL_MOD;

    @Override
    public void onInitialize() {
        VersionChecker.registerMod(C);
    }
}
