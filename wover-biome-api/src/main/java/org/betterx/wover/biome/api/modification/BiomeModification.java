package org.betterx.wover.biome.api.modification;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.impl.api.modification.FeatureMap;
import org.betterx.wover.biome.impl.api.modification.GenerationSettingsWorker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

public class BiomeModification {
    public static final Codec<BiomeModification> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomePredicate.CODEC.fieldOf("predicate").forGetter(p -> p.predicate),
                    FeatureMap.CODEC.fieldOf("features").orElse(new ArrayList<>()).forGetter(p -> p.features.generic())
            ).apply(instance, BiomeModification::new)
    );
    public final BiomePredicate predicate;
    private final FeatureMap features;

    public BiomeModification(BiomePredicate predicate, List<List<Holder<PlacedFeature>>> features) {
        this.predicate = predicate;
        this.features = FeatureMap.of(features);
    }

    public BiomeModification(BiomePredicate predicate) {
        this(predicate, new ArrayList<>(GenerationStep.Decoration.values().length));
    }

    public BiomeModification addFeature(
            GenerationStep.Decoration decoration,
            Holder<PlacedFeature> holder
    ) {
        this.features.getFeatures(decoration).add(holder);
        return this;
    }

    public void apply(GenerationSettingsWorker context) {
        context.addFeatures(features);
    }
}
