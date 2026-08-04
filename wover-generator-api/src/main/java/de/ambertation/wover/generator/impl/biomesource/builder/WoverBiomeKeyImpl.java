package de.ambertation.wover.generator.impl.biomesource.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class WoverBiomeKeyImpl extends BiomeKey<WoverBiomeBuilder.WoverBiome> {
    public WoverBiomeKeyImpl(@NotNull ResourceLocation location) {
        super(location);
    }

    @Override
    public WoverBiomeBuilder.WoverBiome bootstrap(BiomeBootstrapContext context) {
        return new WoverBiomeBuilderImpl(context, this);
    }
}
