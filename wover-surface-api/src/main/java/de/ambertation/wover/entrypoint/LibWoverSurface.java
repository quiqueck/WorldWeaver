package de.ambertation.wover.entrypoint;


import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.surface.impl.SurfaceRuleRegistryImpl;
import de.ambertation.wover.surface.impl.conditions.MaterialConditionRegistryImpl;
import de.ambertation.wover.surface.impl.numeric.NumericProviderRegistryImpl;
import de.ambertation.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverSurface implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-surface", "wover");

    @Override
    public void onInitialize() {
        NumericProviderRegistryImpl.bootstrap();
        MaterialConditionRegistryImpl.bootstrap();
        MaterialRuleRegistryImpl.bootstrap();
        SurfaceRuleRegistryImpl.initialize();
    }
}