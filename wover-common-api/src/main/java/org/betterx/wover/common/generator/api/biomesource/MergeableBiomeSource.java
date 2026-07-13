package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.Set;

/**
 * Marks a {@link BiomeSource} that can merge its set of possible biomes with another {@link BiomeSource}, for
 * example when another mod's generator is installed alongside this one and both should contribute biomes.
 *
 * @param <B> The concrete {@link BiomeSource} type
 */
public interface MergeableBiomeSource<B extends BiomeSource> {
    /**
     * Checks if {@code inputBiomeSource} provides a different set of possible biomes than this one, and merging
     * would therefore change the result.
     *
     * @param inputBiomeSource The {@link BiomeSource} to compare against
     * @return {@code true} if the two sources should be merged
     * @throws IllegalStateException if the possible biomes of either source could not be determined
     */
    default boolean shouldMergeWith(BiomeSource inputBiomeSource) throws IllegalStateException {
        Set<Holder<Biome>> mySet = ((B) this).possibleBiomes();
        try {
            Set<Holder<Biome>> otherSet = inputBiomeSource.possibleBiomes();

            if (otherSet.size() != mySet.size()) return true;

            for (Holder<Biome> b : mySet) {
                if (!otherSet.contains(b))
                    return true;
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to merge BiomeSource", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to merge BiomeSource", e);
        }

        return false;
    }

    /**
     * Returns a BiomeSource that merges the settings of this one with the Biomes (and possibly settings) from the
     * {@code inputBiomeSource}.
     *
     * @param inputBiomeSource The {@link BiomeSource} you want to copy
     * @return The merged or new BiomeSource
     */
    B mergeWithBiomeSource(BiomeSource inputBiomeSource);

}

