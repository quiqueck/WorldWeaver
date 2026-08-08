package de.ambertation.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.stream.Stream;

/**
 * Places multiple features  (<b>{@code wover:sequence}</b>).
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}, so the former
 * {@code SequenceFeatureConfig} was merged into this class: the feature now holds the list of
 * {@link PlacedFeature}s itself and provides its own {@link MapCodec}. The serialized field name
 * ({@code features}) is unchanged, but it is no longer nested inside a {@code config} object because 26.3
 * dispatches feature types on a {@link MapCodec} directly.
 * <p>
 * Note that vanilla now ships its own {@code minecraft:sequence} feature. This one is kept because it
 * predates it and is referenced by existing WorldWeaver data.
 *
 * @see de.ambertation.wover.feature.api.Features#SEQUENCE
 */
public record SequenceFeature(List<Holder<PlacedFeature>> features) implements Feature {
    /**
     * The codec for this feature.
     */
    public static final MapCodec<SequenceFeature> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.nonEmptyList(PlacedFeature.CODEC.listOf())
                               .fieldOf("features")
                               .forGetter(SequenceFeature::features)
            ).apply(instance, SequenceFeature::new)
    );

    /**
     * Creates a new {@link SequenceFeature}.
     *
     * @param features The features to place.
     * @return A new {@link SequenceFeature}.
     */
    public static SequenceFeature createSequence(List<Holder<PlacedFeature>> features) {
        return new SequenceFeature(features);
    }

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<SequenceFeature> codec() {
        return CODEC;
    }

    /**
     * The features this one delegates to. 26.3 uses this to walk the feature graph (for example to collect
     * the bone-meal features of a biome), so it must report every nested feature.
     *
     * @return the features of every {@link PlacedFeature} in this sequence
     */
    @Override
    public Stream<Holder<Feature>> getSubFeatures() {
        return features.stream().flatMap(f -> f.value().getFeatures());
    }

    /**
     * Try to place all features, in order.
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param random    the random source
     * @param pos       the position to place at
     * @return {@code true} if at least one feature was placed
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        boolean placed = false;
        for (Holder<PlacedFeature> f : features) {
            placed |= f.value().place(level, generator, random, pos);
        }
        return placed;
    }
}
