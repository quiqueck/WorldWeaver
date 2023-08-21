package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.impl.builder.WrappedBiomeBuilderImpl;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class WrappedKeyImpl extends BiomeKey<BiomeBuilder.Wrapped> {
    protected WrappedKeyImpl(@NotNull ResourceLocation location) {
        super(location);
    }

    @Override
    public BiomeBuilder.Wrapped bootstrap(BiomeBootstrapContext context) {
        return new WrappedBiomeBuilderImpl(context, this);
    }
}
