package org.betterx.wover.surface.api;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.surface.impl.AssignedSurfaceRule;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

public class SurfaceRuleRegistry {
    /**
     * This event is fired, after the surface rule registry is being loaded. At this point, the
     * registry has loaded all surface rules from datapacks and is not yet frozen
     */
    public static final Event<OnBootstrapRegistry<AssignedSurfaceRule>> BOOTSTRAP_SURFACE_RULE_REGISTRY
            = SurfaceRuleRegistryImpl.BOOTSTRAP_SURFACE_RULE_REGISTRY;
    public static final ResourceKey<Registry<AssignedSurfaceRule>> SURFACE_RULES_REGISTRY =
            createRegistryKey(WoverSurface.C.id("wover/surface_rules"));

    private static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation location) {
        return ResourceKey.createRegistryKey(location);
    }

    public static ResourceKey<AssignedSurfaceRule> createKey(
            ResourceLocation ruleID
    ) {
        return SurfaceRuleRegistryImpl.createKey(ruleID);
    }

    public static Holder<AssignedSurfaceRule> register(
            @NotNull BootstapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            @NotNull ResourceKey<Biome> biomeKey,
            @NotNull SurfaceRules.RuleSource rules
    ) {
        return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, rules);
    }
}
