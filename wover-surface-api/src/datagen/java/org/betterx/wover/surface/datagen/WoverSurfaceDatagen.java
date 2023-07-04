package org.betterx.wover.surface.datagen;

import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

public class WoverSurfaceDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitialize() {
        addRegistryProvider(new NoiseRegistryProvider());
    }

}
