package org.betterx.wover.generator.impl.biomesource.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;

import org.jetbrains.annotations.ApiStatus;

public class WoverBiomeBuilderImpl extends WoverBiomeBuilder.WoverBiome {

    @ApiStatus.Internal
    public WoverBiomeBuilderImpl(BiomeBootstrapContext context, BiomeKey<WoverBiome> key) {
        super(context, key);
    }
}
