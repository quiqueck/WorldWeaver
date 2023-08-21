package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.generator.impl.biomesource.builder.WoverBiomeKeyImpl;
import org.betterx.wover.generator.impl.biomesource.builder.WrappedWoverBiomeKeyImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

public interface WoverBiomeBuilder<B extends BiomeBuilder<B>> {

    B edge(ResourceKey<Biome> edge);
    B parent(ResourceKey<Biome> parent);
    B terrainHeight(float height);
    B genChance(float weight);
    B edgeSize(int size);
    B vertical(boolean vertical);

    static BiomeKey<Wrapped> wrappedKey(@NotNull ResourceKey<Biome> key) {
        return new WrappedWoverBiomeKeyImpl(key.location());
    }

    static BiomeKey<WoverBiome> biomeKey(@NotNull ResourceLocation location) {
        return new WoverBiomeKeyImpl(location);
    }

    abstract class Wrapped extends BiomeBuilder<Wrapped> implements WoverBiomeBuilder<Wrapped> {
        protected Wrapped(
                BiomeBootstrapContext context,
                BiomeKey<WoverBiomeBuilder.Wrapped> key
        ) {
            super(context, key);
        }
    }

    abstract class WoverBiome extends BiomeBuilder.VanillaBuilder<WoverBiome> implements WoverBiomeBuilder<WoverBiome> {
        protected WoverBiome(
                BiomeBootstrapContext context,
                BiomeKey<WoverBiome> key
        ) {
            super(context, key);
        }
    }
}
