package de.ambertation.wover.biome.impl;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.impl.builder.VanillaBiomeBuilderImpl;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class VanillaKeyImpl extends BiomeKey<BiomeBuilder.Vanilla> {
    VanillaKeyImpl(@NotNull ResourceLocation location) {
        super(location);
    }

    @Override
    public BiomeBuilder.Vanilla bootstrap(BiomeBootstrapContext context) {
        return new VanillaBiomeBuilderImpl(context, this);
    }
}
