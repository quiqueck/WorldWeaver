package de.ambertation.wover.generator.impl.biomesource.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;

import net.minecraft.data.worldgen.BootstrapContext;
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
    public void registerBiome(BootstrapContext<Biome> biomeContext) {

    }

    @Override
    public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
        dataContext.register(
                key.dataKey,
                new WoverBiomeData(
                        fogDensity, key.key, new BiomeGenerationDataContainer(parameters, intendedPlacement),
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
    public WoverBiomeBuilder.Wrapped parent(BiomeKey<?> parent) {
        this.parent = parent.key;
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
