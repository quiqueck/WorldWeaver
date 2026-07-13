package org.betterx.wover.common.generator.api.biomesource;

/**
 * Marks a {@link net.minecraft.world.level.biome.BiomeSource} that needs to know the world seed, for example to
 * seed its own noise generators.
 */
public interface BiomeSourceWithSeed {
    /**
     * Called when the world seed for this {@link net.minecraft.world.level.biome.BiomeSource} is known or changes.
     *
     * @param seed The world seed
     */
    void setSeed(long seed);
}
