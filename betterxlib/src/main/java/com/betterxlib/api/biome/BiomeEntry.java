package com.betterxlib.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A wrapper for biome data that includes registration information.
 */
public class BiomeEntry {
    private final ResourceKey<Biome> key;
    private final Supplier<Biome> biomeSupplier;
    @Nullable
    private Holder<Biome> holder;

    public BiomeEntry(ResourceKey<Biome> key, Supplier<Biome> biomeSupplier) {
        this.key = key;
        this.biomeSupplier = biomeSupplier;
    }

    /**
     * Get the resource key for this biome.
     *
     * @return the resource key
     */
    public ResourceKey<Biome> getKey() {
        return key;
    }

    /**
     * Get the biome instance.
     *
     * @return the biome
     */
    public Biome get() {
        return biomeSupplier.get();
    }

    /**
     * Get the biome supplier.
     *
     * @return the supplier
     */
    public Supplier<Biome> getSupplier() {
        return biomeSupplier;
    }

    /**
     * Set the holder after registration.
     *
     * @param holder the biome holder
     */
    public void setHolder(Holder<Biome> holder) {
        this.holder = holder;
    }

    /**
     * Get the holder if available.
     *
     * @return the holder, or null if not yet registered
     */
    @Nullable
    public Holder<Biome> getHolder() {
        return holder;
    }
}
