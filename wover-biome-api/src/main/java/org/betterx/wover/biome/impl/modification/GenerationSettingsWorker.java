package org.betterx.wover.biome.impl.modification;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class GenerationSettingsWorker {
    private final Registry<ConfiguredWorldCarver<?>> carvers;
    private final Registry<PlacedFeature> features;
    private final BiomeGenerationSettings generationSettings;
    private final Biome biome;
    Map<GenerationStep.Carving, HolderSet<ConfiguredWorldCarver<?>>> customizedCarvers;
    List<HolderSet<PlacedFeature>> customizedFeatures;

    public GenerationSettingsWorker(RegistryAccess registries, Biome biome) {
        this.biome = biome;
        this.generationSettings = biome.getGenerationSettings();
        this.carvers = registries.registryOrThrow(Registries.CONFIGURED_CARVER);
        this.features = registries.registryOrThrow(Registries.PLACED_FEATURE);
    }

    private void unfreezeCarvers() {
        if (customizedCarvers == null) {
            customizedCarvers = new EnumMap<>(GenerationStep.Carving.class);
            generationSettings.carvers = customizedCarvers;
        }
    }

    private void freezeCarvers() {
        if (customizedCarvers != null) {
            generationSettings.carvers = ImmutableMap.copyOf(customizedCarvers);
            customizedCarvers = null;
        }
    }


    private void unfreezeFeatures() {
        if (customizedFeatures == null) {
            customizedFeatures = new LinkedList<>(generationSettings.features);
            generationSettings.features = customizedFeatures;
        }
    }

    private void freezeFeatures() {
        if (customizedFeatures != null) {
            generationSettings.features = ImmutableList.copyOf(customizedFeatures);
            generationSettings.featureSet = Suppliers.memoize(this::createPlacedFeatrueSet);
            generationSettings.flowerFeatures = Suppliers.memoize(this::createFlowerFeatures);
            customizedFeatures = null;
        }
    }

    public boolean finished() {
        boolean res = customizedCarvers != null || customizedFeatures != null;
        freezeCarvers();
        freezeFeatures();
        return res;
    }

    private Set<PlacedFeature> createPlacedFeatrueSet() {
        return getFlatFeatureStream().collect(Collectors.toSet());
    }

    private List<ConfiguredFeature<?, ?>> createFlowerFeatures() {
        return getFlatFeatureStream()
                .flatMap(PlacedFeature::getFeatures)
                .filter((configured) -> configured.feature() == Feature.FLOWER)
                .collect(ImmutableList.toImmutableList());
    }

    @NotNull
    private Stream<PlacedFeature> getFlatFeatureStream() {
        return generationSettings
                .features
                .stream()
                .flatMap(HolderSet::stream)
                .map(Holder::value);
    }

    public void addFeatures(FeatureMap features) {
        boolean hasNewFeatures = false;
        for (int index = 0; index < features.size(); index++) {
            if (index < GenerationStep.Decoration.values().length) {
                if (!features.get(index).isEmpty()) {
                    hasNewFeatures = true;
                    break;
                }
            }
        }
        if (!hasNewFeatures) return;

        unfreezeFeatures();
        for (int index = 0; index < features.size(); index++) {
            if (index < GenerationStep.Decoration.values().length) {
                final LinkedList<Holder<PlacedFeature>> newFeatures = features.get(index);
                if (!newFeatures.isEmpty()) {
                    final GenerationStep.Decoration step = GenerationStep.Decoration.values()[index];

                    List<Holder<PlacedFeature>> featuresInStep = new ArrayList<>(
                            FeatureMap.getFeatures(customizedFeatures, step)
                                      .stream()
                                      .toList()
                    );
                    featuresInStep.addAll(newFeatures);

                    customizedFeatures.set(index, HolderSet.direct(featuresInStep));
                }
            }
        }
    }
}
