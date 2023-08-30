package org.betterx.wover.surface.api;

import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;
import org.betterx.wover.surface.impl.SurfaceRuleBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import org.jetbrains.annotations.NotNull;


/**
 * Simplifies surface rule building and registration.
 * <p>
 * If you do not want to use the surface Builder, you can use
 * {@link SurfaceRuleRegistry#register(BootstapContext, ResourceKey, ResourceKey, RuleSource, int)}
 * directly to register arbitrary rule sources.
 */
public interface SurfaceRuleBuilder extends BaseSurfaceRuleBuilder<SurfaceRuleBuilder> {

    /**
     * Start the builder
     *
     * @return new {@link SurfaceRuleBuilder} instance.
     */
    static SurfaceRuleBuilder start() {
        return new SurfaceRuleBuilderImpl.StandalonBuilder();
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeKey {@link ResourceKey} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    SurfaceRuleBuilder biome(ResourceKey<Biome> biomeKey);

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeHolder {@link Holder} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    SurfaceRuleBuilder biome(Holder<Biome> biomeHolder);

    /**
     * The {@link ResourceKey} for the biome filter
     *
     * @return {@link ResourceKey} for the {@link Biome}.
     */
    ResourceKey<Biome> biomeKey();

    /**
     * Finalise rule building process.
     *
     * @return {@link RuleSource}.
     */
    RuleSource build();

    /**
     * Register rule in the {@link SurfaceRuleRegistry} with the currently set sort priority (see {@link #sortPriority}).
     *
     * @param ctx The {@link BootstapContext} to register the rule with.
     * @param key The {@link ResourceKey} to register the rule with.
     * @return The {@link Holder} for the registry item.
     * @see SurfaceRuleRegistry#register(BootstapContext, ResourceKey, ResourceKey, RuleSource, int)
     */

    Holder<AssignedSurfaceRule> register(
            @NotNull BootstapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key
    );
}
