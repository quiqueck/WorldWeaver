package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

public class WoverSurfaceDatagenTestMod extends WoverDataGenEntryPoint {

    @Override
    protected void onInitialize() {
        addRegistryProvider(new SurfaceRuleProvider());
    }
}
