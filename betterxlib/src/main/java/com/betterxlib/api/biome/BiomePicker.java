package com.betterxlib.api.biome;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A weighted biome picker for selecting biomes during world generation.
 * <p>
 * This class maintains a list of weighted biome entries and can pick
 * a random biome based on those weights.
 */
public class BiomePicker {
    private final List<Entry> entries = new ArrayList<>();
    private float totalWeight = 0.0f;
    private boolean needsRebuild = false;

    /**
     * Add a biome with a weight.
     *
     * @param biome the biome holder supplier
     * @param weight the weight (higher = more common)
     * @return this picker for chaining
     */
    public BiomePicker add(Supplier<Holder<Biome>> biome, float weight) {
        if (weight > 0) {
            entries.add(new Entry(biome, weight));
            totalWeight += weight;
            needsRebuild = true;
        }
        return this;
    }

    /**
     * Add a biome with a weight.
     *
     * @param biome the biome holder
     * @param weight the weight (higher = more common)
     * @return this picker for chaining
     */
    public BiomePicker add(Holder<Biome> biome, float weight) {
        return add(() -> biome, weight);
    }

    /**
     * Pick a random biome based on weights.
     *
     * @param random the random source
     * @return a biome holder, or null if the picker is empty
     */
    public Holder<Biome> pick(RandomSource random) {
        if (entries.isEmpty()) {
            return null;
        }

        float value = random.nextFloat() * totalWeight;
        float current = 0.0f;

        for (Entry entry : entries) {
            current += entry.weight;
            if (value < current) {
                return entry.biome.get();
            }
        }

        // Fallback to last entry
        return entries.get(entries.size() - 1).biome.get();
    }

    /**
     * Pick a biome based on a specific noise value (0.0-1.0).
     *
     * @param noise the noise value
     * @return a biome holder, or null if the picker is empty
     */
    public Holder<Biome> pickByNoise(float noise) {
        if (entries.isEmpty()) {
            return null;
        }

        float value = Math.abs(noise % 1.0f) * totalWeight;
        float current = 0.0f;

        for (Entry entry : entries) {
            current += entry.weight;
            if (value < current) {
                return entry.biome.get();
            }
        }

        return entries.get(entries.size() - 1).biome.get();
    }

    /**
     * Get the number of biomes in this picker.
     *
     * @return the count
     */
    public int size() {
        return entries.size();
    }

    /**
     * Check if this picker is empty.
     *
     * @return true if no biomes have been added
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Get the total weight of all entries.
     *
     * @return the total weight
     */
    public float getTotalWeight() {
        return totalWeight;
    }

    /**
     * Get all entries in this picker.
     *
     * @return list of entries
     */
    public List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Clear all entries.
     */
    public void clear() {
        entries.clear();
        totalWeight = 0.0f;
    }

    /**
     * An entry in the biome picker.
     */
    public record Entry(Supplier<Holder<Biome>> biome, float weight) {
        /**
         * Get the normalized weight (probability) of this entry.
         *
         * @param totalWeight the total weight of all entries
         * @return the probability (0.0-1.0)
         */
        public float getProbability(float totalWeight) {
            return totalWeight > 0 ? weight / totalWeight : 0.0f;
        }
    }

    /**
     * Create a new empty biome picker.
     *
     * @return a new BiomePicker
     */
    public static BiomePicker create() {
        return new BiomePicker();
    }
}
