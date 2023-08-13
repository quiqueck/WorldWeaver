package org.betterx.wover.biome.api;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

public abstract class BiomeKey<B extends BiomeBuilder<B>> {
    /**
     * The key for the {@link Biome} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Biome> key;

    /**
     * The key for the {@link BiomeData}.
     */
    @NotNull
    public final ResourceKey<BiomeData> dataKey;

    public abstract B bootstrap(BiomeBootstrapContext context);

    protected BiomeKey(@NotNull ResourceLocation location) {
        this.key = BiomeManagerImpl.createKey(location);
        this.dataKey = BiomeDataRegistry.createKey(location);
    }
}
