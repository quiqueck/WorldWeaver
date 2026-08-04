package de.ambertation.wover.generator.api.map;

import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;

/**
 * Constructs a new {@link BiomeMap} for a given seed, biome size and {@link WoverBiomePicker}.
 */
@FunctionalInterface
public interface MapBuilderFunction {
    /**
     * Constructs a new {@link BiomeMap}.
     *
     * @param seed      the world seed
     * @param biomeSize the size of a single biome
     * @param picker    the picker the map should pick Biomes from
     * @return the new map
     */
    BiomeMap create(long seed, int biomeSize, WoverBiomePicker picker);
}
