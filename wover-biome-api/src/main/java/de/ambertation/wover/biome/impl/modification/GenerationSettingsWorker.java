package de.ambertation.wover.biome.impl.modification;

import de.ambertation.wover.biome.api.modification.FeaturePosition;
import de.ambertation.wover.entrypoint.LibWoverFeature;
import de.ambertation.wover.feature.mixin.BiomeGenerationSettingsAccessor;
import de.ambertation.wover.util.MutableHolderSet;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FeatureTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
    private final int[] prependedPerStep = new int[GenerationStep.Decoration.values().length];

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
        } else if (!(generationSettings instanceof BiomeGenerationSettingsAccessor)) {
            LibWoverFeature.C.LOG.error("Cannot unfreeze generation carvers");
        }
    }

    private void freezeCarvers() {
        if (customizedCarvers != null && generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            accessor.wover_setCarvers(customizedCarvers.asDirectHolderSet());
            customizedCarvers = null;
        }
    }


    private void unfreezeFeatures() {
        if (customizedFeatures == null && generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            customizedFeatures = new LinkedList<>(accessor.wover_getFeatures());
            accessor.wover_setFeatures(customizedFeatures);
        } else if (!(generationSettings instanceof BiomeGenerationSettingsAccessor)) {
            LibWoverFeature.C.LOG.error("Cannot unfreeze generation features");
        }
    }

    private void freezeFeatures() {
        if (customizedFeatures != null && generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            accessor.wover_setFeatures(ImmutableList.copyOf(customizedFeatures));
            accessor.wover_setFeatureSet(Suppliers.memoize(this::createPlacedFeatrueSet));
            accessor.wover_setFlowerFeatures(Suppliers.memoize(this::createFlowerFeatures));

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
        // 26.1: Feature.FLOWER was removed; the bone-meal/flower feature list is now derived by filtering on
        // the FeatureTags.CAN_SPAWN_FROM_BONE_MEAL tag (matching BiomeGenerationSettings#boneMealFeatures).
        return getFlatFeatureStream()
                .flatMap(PlacedFeature::getFeatures)
                .filter((feature) -> feature.is(FeatureTags.CAN_SPAWN_FROM_BONE_MEAL))
                .map(Holder::value)
                .collect(ImmutableList.toImmutableList());
    }

    @NotNull
    private Stream<PlacedFeature> getFlatFeatureStream() {
        if (generationSettings instanceof BiomeGenerationSettingsAccessor accessor) {
            return accessor
                    .wover_getFeatures()
                    .stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value);
        } else {
            LibWoverFeature.C.LOG.error("Cannot get flat feature stream from generation settings");
            return Stream.empty();
        }
    }

    public void addFeatures(FeatureMap features, FeaturePosition position) {
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
                    if (position == FeaturePosition.PREPEND) {
                        // At prependedCount, not at 0: a second prepending modification hitting the same
                        // step of the same biome would otherwise insert in front of the first one's
                        // features and silently reverse the two modifications relative to each other.
                        // This worker lives for exactly one biome, so the counter is per biome and step.
                        featuresInStep.addAll(prependedPerStep[index], newFeatures);
                        prependedPerStep[index] += newFeatures.size();
                    } else {
                        featuresInStep.addAll(newFeatures);
                    }

                    customizedFeatures.set(index, HolderSet.direct(featuresInStep));
                }
            }
        }
    }
}
