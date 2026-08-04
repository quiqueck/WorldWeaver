package de.ambertation.wover.surface.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.entrypoint.LibWoverSurface;

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
