package org.betterx.wover.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class SequenceFeatureConfig implements FeatureConfiguration {
    public static final Codec<SequenceFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.nonEmptyList(PlacedFeature.CODEC.listOf())
                               .fieldOf("features")
                               .forGetter(a -> a.features)
            ).apply(instance, SequenceFeatureConfig::new)
    );

    private final List<Holder<PlacedFeature>> features;

    public static SequenceFeatureConfig createSequence(List<Holder<PlacedFeature>> features) {
        return new SequenceFeatureConfig(features);
    }

    public SequenceFeatureConfig(List<Holder<PlacedFeature>> features) {
        this.features = features;
    }

    public boolean placeAll(FeaturePlaceContext<SequenceFeatureConfig> ctx) {
        boolean placed = false;
        for (Holder<PlacedFeature> f : features) {
            placed |= f.value().place(ctx.level(), ctx.chunkGenerator(), ctx.random(), ctx.origin());
        }
        return placed;

    }
}
