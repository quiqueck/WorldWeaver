package de.ambertation.wover.common.generator.api.chunkgenerator;

import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import de.ambertation.wover.common.generator.api.biomesource.MergeableBiomeSource;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * Marks a {@link ChunkGenerator} that can be forcibly installed as the generator for a dimension, replacing (or
 * merging with) whatever generator was loaded from the world's saved level data.
 *
 * @param <G> The concrete {@link ChunkGenerator} type
 */
public interface EnforceableChunkGenerator<G extends ChunkGenerator> {
    /**
     * Installs this generator (or a generator derived from it) as the generator of {@code dimensionKey} in
     * {@code dimensionRegistry}, merging with {@code loadedChunkGenerator} where necessary.
     *
     * @param access                 The current {@link RegistryAccess}
     * @param dimensionKey           The dimension the generator should be enforced for
     * @param dimensionTypeKey       The {@link DimensionType} of that dimension
     * @param loadedChunkGenerator   The generator that was loaded from the world's saved level data
     * @param dimensionRegistry      The registry holding the {@link LevelStem}s of the world
     * @return The (possibly modified) registry of {@link LevelStem}s
     */
    Registry<LevelStem> enforceGeneratorInWorldGenSettings(
            RegistryAccess access,
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            ChunkGenerator loadedChunkGenerator,
            Registry<LevelStem> dimensionRegistry
    );

    /**
     * Determines whether {@code this} and {@code chunkGenerator} are different enough that already generated
     * chunks would need to be repaired (re-evaluated) if {@code chunkGenerator} were replaced by {@code this}.
     *
     * @param chunkGenerator The generator that {@code this} would replace
     * @return {@code true} if a repair is required
     * @throws IllegalStateException if the biome sources of both generators could not be compared
     */
    default boolean togetherShouldRepair(ChunkGenerator chunkGenerator) throws IllegalStateException {
        ChunkGenerator self = (ChunkGenerator) this;
        if (this == chunkGenerator || chunkGenerator == null) return false;

        BiomeSource one = self.getBiomeSource();
        BiomeSource two = chunkGenerator.getBiomeSource();
        if (one == two) return false;

        if (one instanceof BiomeSourceWithConfig<?, ?> ba && two instanceof BiomeSourceWithConfig<?, ?> bb) {
            if (!ba.getBiomeSourceConfig().couldSetWithoutRepair(bb.getBiomeSourceConfig()))
                return true;
        }
        if (one instanceof MergeableBiomeSource<?> ba) {
            if (ba.shouldMergeWith(two))
                return true;
        }

        return !one.getClass().isAssignableFrom(two.getClass()) && !two.getClass().isAssignableFrom(one.getClass());
    }
}
