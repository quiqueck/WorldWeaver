package de.ambertation.wover.biome.impl.modification;

import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.FeaturePosition;
import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeModificationImpl implements BiomeModification {
    @NotNull
    private final BiomePredicate predicate;
    @NotNull
    private final FeatureMap features;
    @NotNull
    private final FeaturePosition featurePosition;
    @Nullable
    private final List<TagKey<Biome>> biomeTags;

    @NotNull
    private final WeightedList<MobSpawnSettings.SpawnerData> spawns;

    public BiomeModificationImpl(
            @NotNull BiomePredicate predicate,
            @NotNull List<List<Holder<PlacedFeature>>> features,
            @Nullable List<TagKey<Biome>> biomeTags,
            @Nullable WeightedList<MobSpawnSettings.SpawnerData> spawns
    ) {
        this(predicate, features, FeaturePosition.APPEND, biomeTags, spawns);
    }

    public BiomeModificationImpl(
            @NotNull BiomePredicate predicate,
            @NotNull List<List<Holder<PlacedFeature>>> features,
            @NotNull FeaturePosition featurePosition,
            @Nullable List<TagKey<Biome>> biomeTags,
            @Nullable WeightedList<MobSpawnSettings.SpawnerData> spawns
    ) {
        this.predicate = predicate;
        this.features = FeatureMap.of(features);
        this.featurePosition = featurePosition;
        this.biomeTags = biomeTags;
        this.spawns = spawns;
    }

    @Override
    public BiomePredicate predicate() {
        return predicate;
    }

    @Override
    public List<TagKey<Biome>> biomeTags() {
        return biomeTags;
    }

    @Override
    public final List<List<Holder<PlacedFeature>>> features() {
        return features.generic();
    }

    @Override
    public FeaturePosition featurePosition() {
        return featurePosition;
    }

    @Override
    public WeightedList<MobSpawnSettings.SpawnerData> spawns() {
        return this.spawns;
    }

    @Override
    public void apply(GenerationSettingsWorker worker, MobSettingsWorker mobWorker) {
        worker.addFeatures(features, featurePosition);
        mobWorker.addSpawns(spawns);
    }
}
