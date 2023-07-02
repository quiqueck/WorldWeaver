package org.betterx.wover.surface.impl;

import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class SurfaceRuleRegistryImpl {
    private static Map<ResourceKey<AssignedSurfaceRule>, AssignedSurfaceRule> KNOWN = new HashMap<>();

    @ApiStatus.Internal
    public static void bootstrap(BootstapContext<AssignedSurfaceRule> ctx) {
        for (var entry : KNOWN.entrySet()) {
            ctx.register(entry.getKey(), entry.getValue());
        }
    }

    @ApiStatus.Internal
    public static void initialize() {
        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(SurfaceRuleUtil::injectSurfaceRulesToAllDimensions, 2000);
    }

    public static ResourceKey<AssignedSurfaceRule> registerRule(
            ResourceLocation ruleID,
            SurfaceRules.RuleSource rules,
            ResourceLocation biomeID
    ) {
        final ResourceKey<AssignedSurfaceRule> key = ResourceKey.create(
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                ruleID
        );

        SurfaceRuleRegistryImpl.KNOWN.put(
                key,
                new AssignedSurfaceRule(
                        SurfaceRules.ifTrue(
                                SurfaceRules.isBiome(ResourceKey.create(Registries.BIOME, biomeID)),
                                rules
                        ), biomeID
                )
        );

        return key;
    }
}
