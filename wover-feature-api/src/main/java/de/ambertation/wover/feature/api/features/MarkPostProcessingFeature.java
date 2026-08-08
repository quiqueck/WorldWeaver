package de.ambertation.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Places a mark for postprocessing  (<b>{@code wover:mark_postprocessing}</b>).
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}: a {@link Feature} now carries its own
 * configuration and its own {@link MapCodec}. This feature has no configuration at all, so it is a record
 * with a unit codec (the former {@code NoneFeatureConfiguration} no longer exists).
 *
 * @see de.ambertation.wover.feature.api.Features#MARK_POSTPROCESSING
 */
public record MarkPostProcessingFeature() implements Feature {
    /**
     * The codec for this feature. It carries no fields.
     */
    public static final MapCodec<MarkPostProcessingFeature> CODEC = MapCodec.unit(MarkPostProcessingFeature::new);

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<MarkPostProcessingFeature> codec() {
        return CODEC;
    }

    /**
     * Places a mark on the chunk for postprocessing.
     *
     * @param level     the level to mark in
     * @param generator the chunk generator
     * @param random    the random source
     * @param pos       the position to mark
     * @return {@code true}
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        level.getChunk(pos.getX() >> 4, pos.getZ() >> 4)
             .markPosForPostProcessing(new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
        return true;
    }
}
