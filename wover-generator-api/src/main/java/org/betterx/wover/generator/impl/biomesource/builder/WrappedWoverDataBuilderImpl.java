package org.betterx.wover.generator.impl.biomesource.builder;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.Nullable;

public class WrappedWoverDataBuilderImpl extends WoverBiomeBuilder.Wrapped {
    private float terrainHeight;
    private float genChance;
    private int edgeSize;
    private boolean vertical;
    private @Nullable ResourceKey<Biome> edge;
    private @Nullable ResourceKey<Biome> parent;

    protected WrappedWoverDataBuilderImpl(
            BiomeBootstrapContext context,
            BiomeKey<WoverBiomeBuilder.Wrapped> key
    ) {
        super(context, key);
        this.genChance = 1.0f;
        this.edgeSize = 0;
        this.terrainHeight = 0.1f;
        this.vertical = false;
    }

    @Override
    public void registerBiome(BootstapContext<Biome> biomeContext) {

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
    public WoverBiomeBuilder.Wrapped edge(ResourceKey<Biome> edge) {
        this.edge = edge;
        return this;
    }

    @Override
    public WoverBiomeBuilder.Wrapped parent(ResourceKey<Biome> parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public WoverBiomeBuilder.Wrapped terrainHeight(float height) {
        this.terrainHeight = height;
        return this;
    }

    @Override
    public WoverBiomeBuilder.Wrapped genChance(float weight) {
        this.genChance = weight;
        return this;
    }

    @Override
    public WoverBiomeBuilder.Wrapped edgeSize(int size) {
        this.edgeSize = size;
        return this;
    }

    @Override
    public WoverBiomeBuilder.Wrapped vertical(boolean vertical) {
        this.vertical = vertical;
        return this;
    }
}
