package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

public class BiomeModificationImpl implements BiomeModification {
    private final BiomePredicate predicate;
    private final FeatureMap features;
    private final Optional<List<TagKey<Biome>>> biomeTags;

    public BiomeModificationImpl(
            BiomePredicate predicate,
            Optional<List<List<Holder<PlacedFeature>>>> features,
            Optional<List<TagKey<Biome>>> biomeTags
    ) {
        this(
                predicate,
                features.orElse(List.of()),
                biomeTags
        );
    }

    public BiomeModificationImpl(
            BiomePredicate predicate,
            List<List<Holder<PlacedFeature>>> features,
            Optional<List<TagKey<Biome>>> biomeTags
    ) {
        this.predicate = predicate;
        this.features = FeatureMap.of(features);
        this.biomeTags = biomeTags;
    }

    @Override
    public BiomePredicate predicate() {
        return predicate;
    }

    @Override
    public Optional<List<TagKey<Biome>>> biomeTags() {
        return biomeTags;
    }

    @Override
    public final List<List<Holder<PlacedFeature>>> features() {
        return features.generic();
    }

    @Override
    public void apply(GenerationSettingsWorker worker) {
        worker.addFeatures(features);
    }
}
