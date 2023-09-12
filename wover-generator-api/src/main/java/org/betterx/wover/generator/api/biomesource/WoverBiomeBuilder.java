package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.generator.impl.biomesource.builder.WoverBiomeKeyImpl;
import org.betterx.wover.generator.impl.biomesource.builder.WrappedWoverBiomeKeyImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    abstract class WoverBiome extends AbstractWoverBiomeBuilder<WoverBiome> {
        protected WoverBiome(BiomeBootstrapContext context, BiomeKey<WoverBiome> key) {
            super(context, key);
        }
    }

    abstract class AbstractWoverBiomeBuilder<T extends AbstractWoverBiomeBuilder<T>> extends BiomeBuilder.VanillaBuilder<T> implements WoverBiomeBuilder<T> {
        protected float terrainHeight;
        protected float genChance;
        protected int edgeSize;
        protected boolean vertical;
        protected @Nullable ResourceKey<Biome> edge;
        protected @Nullable ResourceKey<Biome> parent;

        protected AbstractWoverBiomeBuilder(
                BiomeBootstrapContext context,
                BiomeKey<T> key
        ) {
            super(context, key);
            this.genChance = 1.0f;
            this.edgeSize = 0;
            this.terrainHeight = 0.1f;
            this.vertical = false;
        }

        @Override
        public void registerBiomeData(BootstapContext<BiomeData> dataContext) {
            dataContext.register(
                    key.dataKey,
                    new WoverBiomeData(
                            fogDensity, key.key, parameters,
                            terrainHeight, genChance, edgeSize, vertical, edge, parent
                    )
            );
        }

        @Override
        public T edge(ResourceKey<Biome> edge) {
            this.edge = edge;
            return (T) this;
        }

        @Override
        public T parent(ResourceKey<Biome> parent) {
            this.parent = parent;
            return (T) this;
        }

        @Override
        public T terrainHeight(float height) {
            this.terrainHeight = height;
            return (T) this;
        }

        @Override
        public T genChance(float weight) {
            this.genChance = weight;
            return (T) this;
        }

        @Override
        public T edgeSize(int size) {
            this.edgeSize = size;
            return (T) this;
        }

        @Override
        public T vertical(boolean vertical) {
            this.vertical = vertical;
            return (T) this;
        }
    }
}
