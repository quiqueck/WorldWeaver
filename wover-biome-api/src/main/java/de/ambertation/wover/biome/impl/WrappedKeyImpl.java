package de.ambertation.wover.biome.impl;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.impl.builder.WrappedBiomeBuilderImpl;

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
