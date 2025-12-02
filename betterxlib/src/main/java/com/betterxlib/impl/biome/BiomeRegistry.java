package com.betterxlib.impl.biome;

import com.betterxlib.BetterXLib;
import com.betterxlib.api.biome.BiomeEntry;
import com.betterxlib.api.biome.BiomePicker;
import com.betterxlib.api.biome.EndBiomePlacement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

/**
 * Internal registry for managing custom biome registrations.
 */
public final class BiomeRegistry {

    // End biome pickers by placement type
    private static final Map<EndBiomePlacement, BiomePicker> END_BIOMES = new EnumMap<>(EndBiomePlacement.class);

    // Nether biome picker
    private static final BiomePicker NETHER_BIOMES = BiomePicker.create();

    // Lists for pending registrations
    private static final List<PendingEndBiome> PENDING_END_BIOMES = new ArrayList<>();
    private static final List<PendingNetherBiome> PENDING_NETHER_BIOMES = new ArrayList<>();

    // Track all registered biome entries
    private static final Map<String, BiomeEntry> ALL_BIOMES = new HashMap<>();

    static {
        // Initialize End biome pickers for each placement type
        for (EndBiomePlacement placement : EndBiomePlacement.values()) {
            END_BIOMES.put(placement, BiomePicker.create());
        }
    }

    private BiomeRegistry() {
        // Utility class
    }

    /**
     * Register an End biome.
     *
     * @param biome the biome entry
     * @param placement the placement type
     * @param weight the weight
     */
    public static void registerEndBiome(BiomeEntry biome, EndBiomePlacement placement, float weight) {
        PENDING_END_BIOMES.add(new PendingEndBiome(biome, placement, weight));
        ALL_BIOMES.put(biome.getKey().location().toString(), biome);
        BetterXLib.LOGGER.debug("Queued End biome registration: {} -> {} (weight: {})",
            biome.getKey().location(), placement, weight);
    }

    /**
     * Register a Nether biome.
     *
     * @param biome the biome entry
     * @param weight the weight
     */
    public static void registerNetherBiome(BiomeEntry biome, float weight) {
        PENDING_NETHER_BIOMES.add(new PendingNetherBiome(biome, weight));
        ALL_BIOMES.put(biome.getKey().location().toString(), biome);
        BetterXLib.LOGGER.debug("Queued Nether biome registration: {} (weight: {})",
            biome.getKey().location(), weight);
    }

    /**
     * Process pending registrations. Called after registry events.
     */
    public static void processPendingRegistrations() {
        BetterXLib.LOGGER.info("Processing pending biome registrations...");

        for (PendingEndBiome pending : PENDING_END_BIOMES) {
            Holder<Biome> holder = pending.entry.getHolder();
            if (holder != null) {
                END_BIOMES.get(pending.placement).add(holder, pending.weight);
                BetterXLib.LOGGER.debug("Registered End biome: {} in {}",
                    pending.entry.getKey().location(), pending.placement);
            } else {
                BetterXLib.LOGGER.warn("Failed to register End biome: {} - holder not set",
                    pending.entry.getKey().location());
            }
        }

        for (PendingNetherBiome pending : PENDING_NETHER_BIOMES) {
            Holder<Biome> holder = pending.entry.getHolder();
            if (holder != null) {
                NETHER_BIOMES.add(holder, pending.weight);
                BetterXLib.LOGGER.debug("Registered Nether biome: {}",
                    pending.entry.getKey().location());
            } else {
                BetterXLib.LOGGER.warn("Failed to register Nether biome: {} - holder not set",
                    pending.entry.getKey().location());
            }
        }

        int endCount = PENDING_END_BIOMES.size();
        int netherCount = PENDING_NETHER_BIOMES.size();

        PENDING_END_BIOMES.clear();
        PENDING_NETHER_BIOMES.clear();

        BetterXLib.LOGGER.info("Processed {} End biomes and {} Nether biomes", endCount, netherCount);
    }

    /**
     * Get the biome picker for an End placement type.
     *
     * @param placement the placement type
     * @return the biome picker
     */
    public static BiomePicker getEndBiomePicker(EndBiomePlacement placement) {
        return END_BIOMES.get(placement);
    }

    /**
     * Get the Nether biome picker.
     *
     * @return the biome picker
     */
    public static BiomePicker getNetherBiomePicker() {
        return NETHER_BIOMES;
    }

    /**
     * Get all registered biome entries.
     *
     * @return unmodifiable map of all biomes
     */
    public static Map<String, BiomeEntry> getAllBiomes() {
        return Collections.unmodifiableMap(ALL_BIOMES);
    }

    /**
     * Get a biome entry by its resource location string.
     *
     * @param key the resource location as a string
     * @return the biome entry, or null if not found
     */
    public static BiomeEntry getBiome(String key) {
        return ALL_BIOMES.get(key);
    }

    private record PendingEndBiome(BiomeEntry entry, EndBiomePlacement placement, float weight) {}
    private record PendingNetherBiome(BiomeEntry entry, float weight) {}
}
