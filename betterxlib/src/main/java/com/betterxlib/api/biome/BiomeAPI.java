package com.betterxlib.api.biome;

import com.betterxlib.BetterXLib;
import com.betterxlib.impl.biome.BiomeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

/**
 * Main entry point for biome operations in BetterXLib.
 * <p>
 * Provides methods for registering custom biomes in the End and Nether dimensions,
 * as well as utilities for querying and modifying biome properties.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Register an End biome
 * BiomeAPI.registerEndBiome(
 *     EndBiomeBuilder.create(modId, "crystal_plains")
 *         .temperature(0.5f)
 *         .fogColor(0x8080FF)
 *         .build(),
 *     EndBiomePlacement.END_LAND,
 *     0.5f
 * );
 *
 * // Register a Nether biome
 * BiomeAPI.registerNetherBiome(
 *     NetherBiomeBuilder.create(modId, "crimson_caves")
 *         .temperature(2.0f)
 *         .fogColor(0xFF0000)
 *         .build(),
 *     0.3f
 * );
 * }</pre>
 */
public final class BiomeAPI {

    private BiomeAPI() {
        // Utility class
    }

    /**
     * Register a custom End biome.
     *
     * @param biome the biome to register
     * @param placement where in the End this biome should appear
     * @param weight the weight for biome selection (higher = more common)
     */
    public static void registerEndBiome(BiomeEntry biome, EndBiomePlacement placement, float weight) {
        BiomeRegistry.registerEndBiome(biome, placement, weight);
        if (BetterXLib.LOGGER.isDebugEnabled()) {
            BetterXLib.LOGGER.debug("Registered End biome: {} with placement {} and weight {}",
                biome.getKey().location(), placement, weight);
        }
    }

    /**
     * Register a custom Nether biome.
     *
     * @param biome the biome to register
     * @param weight the weight for biome selection (higher = more common)
     */
    public static void registerNetherBiome(BiomeEntry biome, float weight) {
        BiomeRegistry.registerNetherBiome(biome, weight);
        if (BetterXLib.LOGGER.isDebugEnabled()) {
            BetterXLib.LOGGER.debug("Registered Nether biome: {} with weight {}",
                biome.getKey().location(), weight);
        }
    }

    /**
     * Create a biome resource key.
     *
     * @param modId the mod ID
     * @param name the biome name
     * @return the resource key for the biome
     */
    public static ResourceKey<Biome> createKey(String modId, String name) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(modId, name));
    }

    /**
     * Create a biome resource key from a resource location.
     *
     * @param location the resource location
     * @return the resource key for the biome
     */
    public static ResourceKey<Biome> createKey(ResourceLocation location) {
        return ResourceKey.create(Registries.BIOME, location);
    }

    /**
     * Get a biome holder from a registry and key.
     *
     * @param registry the biome registry
     * @param key the biome key
     * @return an optional containing the biome holder, or empty if not found
     */
    public static Optional<Holder.Reference<Biome>> getBiome(Registry<Biome> registry, ResourceKey<Biome> key) {
        return registry.getHolder(key);
    }

    /**
     * Check if a biome is registered.
     *
     * @param registry the biome registry
     * @param key the biome key
     * @return true if the biome exists
     */
    public static boolean biomeExists(Registry<Biome> registry, ResourceKey<Biome> key) {
        return registry.containsKey(key);
    }
}
