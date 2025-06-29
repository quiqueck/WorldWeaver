package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.feature.mixin.BiomeGenerationSettingsAccessor;
import org.betterx.wover.util.MutableHolderSet;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class GenerationSettingsWorker {
    private final Registry<ConfiguredWorldCarver<?>> carverLookup;
    private final Registry<PlacedFeature> featureLookup;
    private final BiomeGenerationSettings generationSettings;
    private final Biome biome;
    MutableHolderSet<ConfiguredWorldCarver<?>> customizedCarvers;
    List<HolderSet<PlacedFeature>> customizedFeatures;

    public GenerationSettingsWorker(RegistryAccess registries, Biome biome) {
        this.biome = biome;
        this.generationSettings = biome.getGenerationSettings();
        this.carverLookup = registries.lookupOrThrow(Registries.CONFIGURED_CARVER);
        this.featureLookup = registries.lookupOrThrow(Registries.PLACED_FEATURE);
    }

    private void unfreezeCarvers() {
        if (customizedCarvers == null && generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            customizedCarvers = MutableHolderSet.of(accessor.wover_getCarvers());
            accessor.wover_setCarvers(customizedCarvers);
        }
    }

    private void freezeCarvers() {
        if (customizedCarvers != null && generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            accessor.wover_setCarvers(customizedCarvers.asDirectHolderSet());
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
