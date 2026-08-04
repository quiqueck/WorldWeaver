package de.ambertation.wover.surface.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SurfaceRuleRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<AssignedSurfaceRule>> BOOTSTRAP_SURFACE_RULE_REGISTRY
            = new EventImpl<>("BOOTSTRAP_SURFACE_RULE_REGISTRY");

    private static void onBootstrap(BootstrapContext<AssignedSurfaceRule> ctx) {
        BOOTSTRAP_SURFACE_RULE_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.register(
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                AssignedSurfaceRuleImpl.CODEC,
                SurfaceRuleRegistryImpl::onBootstrap
        );

        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(SurfaceRuleUtil::injectSurfaceRulesToAllDimensions, 500);
    }

    public static ResourceKey<AssignedSurfaceRule> createKey(
            ResourceLocation ruleID
    ) {
        return ResourceKey.create(
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY,
                ruleID
        );
    }

    @ApiStatus.Internal
    public static Holder<AssignedSurfaceRule> register(
            @NotNull BootstrapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            @NotNull ResourceKey<Biome> biomeKey,
            @NotNull SurfaceRules.RuleSource rules,
            int priority
    ) {
        if (biomeKey == null) {
            throw new IllegalStateException("Biome key is not set for surface rule '" + key.location() + "'");
        }

        return ctx.register(
                key,
                new AssignedSurfaceRuleImpl(rules, biomeKey.location(), priority)
        );
    }
}
