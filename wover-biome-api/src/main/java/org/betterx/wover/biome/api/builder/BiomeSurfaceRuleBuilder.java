package org.betterx.wover.biome.api.builder;

import org.betterx.wover.surface.impl.BaseSurfaceRuleBuilder;

public interface BiomeSurfaceRuleBuilder<B extends BiomeBuilder<B>> extends BaseSurfaceRuleBuilder<BiomeSurfaceRuleBuilder<B>> {
    B finishSurface();
}
