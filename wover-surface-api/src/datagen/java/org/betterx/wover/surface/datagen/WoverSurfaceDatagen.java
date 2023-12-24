package org.betterx.wover.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.entrypoint.LibWoverSurface;

public class WoverSurfaceDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack
                .addRegistryProvider(NoiseRegistryProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return LibWoverSurface.C;
    }

}
