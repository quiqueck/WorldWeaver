package de.ambertation.wover.surface.api;

import de.ambertation.wover.surface.impl.BaseSurfaceRuleBuilder;
import de.ambertation.wover.surface.impl.SurfaceRuleBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import org.jetbrains.annotations.NotNull;


/**
 * Simplifies surface rule building and registration.
 * <p>
 * If you do not want to use the surface Builder, you can use
 * {@link SurfaceRuleRegistry#register(BootstrapContext, ResourceKey, ResourceKey, RuleSource, int)}
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
     * <p>
     * Since Minecraft 26.2 {@link net.minecraft.world.level.levelgen.SurfaceRules#isBiome(HolderGetter, ResourceKey...)}
     * resolves its biome keys into a {@link net.minecraft.core.HolderSet} rather than keeping the raw
     * keys, so composing the biome filter now requires a biome lookup. Inside a registry bootstrap use
     * {@code ctx.lookup(Registries.BIOME)}; at runtime use the {@link net.minecraft.core.RegistryAccess}.
     *
     * @param biomes The lookup used to resolve {@link #biomeKey()}. Only consulted when a biome filter
     *               was set on this builder.
     * @return {@link RuleSource}.
     */
    RuleSource build(HolderGetter<Biome> biomes);

    /**
     * Register rule in the {@link SurfaceRuleRegistry} with the currently set sort priority (see {@link #sortPriority}).
     *
     * @param ctx The {@link BootstrapContext} to register the rule with.
     * @param key The {@link ResourceKey} to register the rule with.
     * @return The {@link Holder} for the registry item.
     * @see SurfaceRuleRegistry#register(BootstrapContext, ResourceKey, ResourceKey, RuleSource, int)
     */

    Holder<AssignedSurfaceRule> register(
            @NotNull BootstrapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key
    );
}
