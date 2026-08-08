package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.features.GrowableFeature;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.Nullable;

public class FeatureContentManagerImpl {
    public static boolean placeInWorld(
            Feature feature,
            WorldGenLevel level,
            BlockPos pos,
            RandomSource random,
            @Nullable ChunkGenerator chunkGenerator,
            boolean asIs
    ) {
        if (!asIs) {
            // A wover:random_patch only wraps another feature. Growing (as opposed to worldgen placement)
            // should apply to the feature inside the patch, so unwrap one level first.
            if (feature instanceof RandomPatchFeature rnd) {
                feature = rnd.feature().value().feature().value();
            }

            if (feature instanceof GrowableFeature growable) {
                return growable.grow(level, pos, random);
            }
        }

        if (chunkGenerator == null && level instanceof ServerLevel sLevel) {
            chunkGenerator = sLevel.getChunkSource().getGenerator();
        }

        // 26.3 removed FeaturePlaceContext: Feature#place takes the level, generator, random and origin
        // directly, because the configuration now lives on the feature itself.
        return feature.place(level, chunkGenerator, random, pos);
    }
}
