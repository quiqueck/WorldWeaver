package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
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
    @Nullable
    private final List<TagKey<Biome>> biomeTags;

    @NotNull
    private final List<MobSpawnSettings.SpawnerData> spawns;

    public BiomeModificationImpl(
            @NotNull BiomePredicate predicate,
            @NotNull List<List<Holder<PlacedFeature>>> features,
            @Nullable List<TagKey<Biome>> biomeTags,
            @Nullable List<MobSpawnSettings.SpawnerData> spawns
    ) {
        this.predicate = predicate;
        this.features = FeatureMap.of(features);
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
    public List<MobSpawnSettings.SpawnerData> spawns() {
        return this.spawns;
    }

    @Override
    public void apply(GenerationSettingsWorker worker, MobSettingsWorker mobWorker) {
        worker.addFeatures(features);
        mobWorker.addSpawns(spawns);
    }
}
