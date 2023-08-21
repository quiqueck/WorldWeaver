package org.betterx.wover.common.generator.api.chunkgenerator;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.common.generator.api.biomesource.MergeableBiomeSource;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public interface EnforceableChunkGenerator<G extends ChunkGenerator> {
    Registry<LevelStem> enforceGeneratorInWorldGenSettings(
            RegistryAccess access,
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            ChunkGenerator loadedChunkGenerator,
            Registry<LevelStem> dimensionRegistry
    );

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
