package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class BiomeModificationImpl implements BiomeModification {
    private final BiomePredicate predicate;
    private final FeatureMap features;

    public BiomeModificationImpl(BiomePredicate predicate, List<List<Holder<PlacedFeature>>> features) {
        this.predicate = predicate;
        this.features = FeatureMap.of(features);
    }

    public BiomePredicate predicate() {
        return predicate;
    }

    public final List<List<Holder<PlacedFeature>>> features() {
        return features.generic();
    }

    public void apply(GenerationSettingsWorker worker) {
        worker.addFeatures(features);
    }
}
