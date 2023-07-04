package org.betterx.wover.entrypoint;


import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;
import org.betterx.wover.surface.impl.conditions.MaterialConditionRegistryImpl;
import org.betterx.wover.surface.impl.numeric.NumericProviderRegistryImpl;
import org.betterx.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import net.fabricmc.api.ModInitializer;

public class WoverSurface implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-surface", "wover");

    @Override
    public void onInitialize() {
        NumericProviderRegistryImpl.bootstrap();
        MaterialConditionRegistryImpl.bootstrap();
        MaterialRuleRegistryImpl.bootstrap();
        SurfaceRuleRegistryImpl.initialize();
    }
}