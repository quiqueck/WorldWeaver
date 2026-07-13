package org.betterx.wover.biome.api.builder;

import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

/**
 * A {@link BaseSurfaceRuleBuilder} that is bound to a {@link BiomeBuilder}, used to define the surface rule
 * of a Biome created through this API.
 * <p>
 * Obtained by calling {@link BiomeBuilder#startSurface()}; every rule set through the inherited
 * {@link BaseSurfaceRuleBuilder} methods only applies to the Biome the builder was started from. Call
 * {@link #finishSurface()} to return to the {@link BiomeBuilder}.
 *
 * @param <B> The type of the {@link BiomeBuilder} this surface rule builder belongs to.
 */
public interface BiomeSurfaceRuleBuilder<B extends BiomeBuilder<B>> extends BaseSurfaceRuleBuilder<BiomeSurfaceRuleBuilder<B>> {
    /**
     * Finishes the surface rule and returns to the {@link BiomeBuilder} it was created from.
     *
     * @return The {@link BiomeBuilder} this surface rule builder belongs to.
     */
    B finishSurface();
}
