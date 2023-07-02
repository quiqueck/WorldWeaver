package org.betterx.wover.surface.api;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.surface.impl.AssignedSurfaceRule;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class SurfaceRuleRegistry {
    public static final ResourceKey<Registry<AssignedSurfaceRule>> SURFACE_RULES_REGISTRY =
            createRegistryKey(WoverSurface.C.id("wover/surface_rules"));

    private static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation location) {
        return ResourceKey.createRegistryKey(location);
    }

    public static ResourceKey<AssignedSurfaceRule> registerRule(
            ResourceLocation ruleID,
            SurfaceRules.RuleSource rules,
            ResourceLocation biomeID
    ) {
        return SurfaceRuleRegistryImpl.registerRule(ruleID, rules, biomeID);
    }
}
