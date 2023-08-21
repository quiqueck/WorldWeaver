package org.betterx.wover.generator.api.map;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;

@FunctionalInterface
public interface MapBuilderFunction {
    BiomeMap create(long seed, int biomeSize, WoverBiomePicker picker);
}
