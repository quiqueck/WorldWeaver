package de.ambertation.wover.common.generator.api.biomesource;

/**
 * Marks a {@link net.minecraft.world.level.biome.BiomeSource} whose set of possible biomes can be reloaded, for
 * example after a datapack or resource reload changed the underlying biome registry.
 */
public interface ReloadableBiomeSource {
    /**
     * Reloads the biomes that are managed by this {@link net.minecraft.world.level.biome.BiomeSource}.
     */
    void reloadBiomes();
}
