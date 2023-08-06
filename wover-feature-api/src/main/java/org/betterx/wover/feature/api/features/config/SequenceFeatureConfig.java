package org.betterx.wover.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

/**
 * Config for {@link org.betterx.wover.feature.api.features.SequenceFeature}. The configuration holds
 * a list of {@link PlacedFeature}s that will be placed in order.
 */
public class SequenceFeatureConfig implements FeatureConfiguration {
    /**
     * Codec for {@link SequenceFeatureConfig}.
     */
    public static final Codec<SequenceFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.nonEmptyList(PlacedFeature.CODEC.listOf())
                               .fieldOf("features")
                               .forGetter(a -> a.features)
            ).apply(instance, SequenceFeatureConfig::createSequence)
    );

    private final List<Holder<PlacedFeature>> features;

    /**
     * Creates a new {@link SequenceFeatureConfig}.
     *
     * @param features The features to place.
     * @return A new {@link SequenceFeatureConfig}.
     */
    public static SequenceFeatureConfig createSequence(List<Holder<PlacedFeature>> features) {
        return new SequenceFeatureConfig(features);
    }

    private SequenceFeatureConfig(List<Holder<PlacedFeature>> features) {
        this.features = features;
    }

    /**
     * calls {@link PlacedFeature#place(WorldGenLevel, ChunkGenerator, RandomSource, BlockPos)} on all
     * features in order.
     *
     * @param ctx The placement context.
     * @return {@code true} if at least one feature was placed, {@code false} otherwise.
     */
    public boolean placeAll(FeaturePlaceContext<SequenceFeatureConfig> ctx) {
        boolean placed = false;
        for (Holder<PlacedFeature> f : features) {
            placed |= f.value().place(ctx.level(), ctx.chunkGenerator(), ctx.random(), ctx.origin());
        }
        return placed;

    }
}
