package org.betterx.wover.generator.impl.biomesource.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class WrappedWoverBiomeKeyImpl extends BiomeKey<WoverBiomeBuilder.Wrapped> {
    public WrappedWoverBiomeKeyImpl(@NotNull ResourceLocation location) {
        super(location);
    }

    @Override
    public WoverBiomeBuilder.Wrapped bootstrap(BiomeBootstrapContext context) {
        return new WrappedWoverDataBuilderImpl(context, this);
    }
}
