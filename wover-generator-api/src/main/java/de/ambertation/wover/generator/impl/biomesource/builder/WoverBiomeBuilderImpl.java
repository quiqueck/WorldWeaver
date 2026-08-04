package de.ambertation.wover.generator.impl.biomesource.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;

import org.jetbrains.annotations.ApiStatus;

public class WoverBiomeBuilderImpl extends WoverBiomeBuilder.WoverBiome {

    @ApiStatus.Internal
    public WoverBiomeBuilderImpl(BiomeBootstrapContext context, BiomeKey<WoverBiome> key) {
        super(context, key);
    }
}
