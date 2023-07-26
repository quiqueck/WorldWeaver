package org.betterx.wover.surface.impl;

import org.betterx.wover.common.surface.api.InjectableSurfaceRules;
import org.betterx.wover.common.surface.api.SurfaceRuleProvider;
import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;

public class SurfaceRuleUtil {
    private static List<SurfaceRules.RuleSource> getRulesForBiome(ResourceKey<Biome> biomeKey) {
        Registry<AssignedSurfaceRule> registry = null;
        if (WorldState.registryAccess() != null)
            registry = WorldState.registryAccess()
                                 .registryOrThrow(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);

        if (registry == null) return List.of();

        var list = registry.stream()
                           .filter(a -> a != null && a.biomeID != null && a.biomeID.equals(biomeKey.location()))
                           .sorted((a, b) -> b.priority - a.priority)
                           .map(a -> a.ruleSource)
                           .toList();

        if (list.size() == 0) return List.of();
        
        return List.of(SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey), new SurfaceRules.SequenceRuleSource(list)));
    }

    private static List<SurfaceRules.RuleSource> getRulesForBiomes(List<Biome> biomes) {
        Registry<Biome> biomeRegistry = WorldState.registryAccess().registryOrThrow(Registries.BIOME);
        List<ResourceKey<Biome>> biomeIDs = biomes.stream()
                                                  .map(biomeRegistry::getResourceKey)
                                                  .filter(Optional::isPresent)
                                                  .map(Optional::get)
                                                  .toList();

        return biomeIDs.stream()
                       .map(SurfaceRuleUtil::getRulesForBiome)
                       .flatMap(List::stream)
                       .collect(Collectors.toCollection(LinkedList::new));
    }

    private static SurfaceRules.RuleSource mergeSurfaceRules(
            ResourceKey<LevelStem> dimensionKey,
            SurfaceRules.RuleSource org,
            BiomeSource source,
            List<SurfaceRules.RuleSource> additionalRules
    ) {
        if (additionalRules == null || additionalRules.isEmpty()) return null;
        Stopwatch sw = Stopwatch.createStarted();
        final int count = additionalRules.size();
        if (org instanceof SurfaceRules.SequenceRuleSource sequenceRule) {
            List<SurfaceRules.RuleSource> existingSequence = sequenceRule.sequence();
            additionalRules = additionalRules
                    .stream()
                    .filter(r -> !existingSequence.contains(r))
                    .collect(Collectors.toList());
            if (additionalRules.isEmpty()) return null;

            // when we are in the nether, we want to keep the nether roof and floor rules in the beginning of the sequence
            // we will add our rules whne the first biome test sequence is found
            if (dimensionKey.equals(LevelStem.NETHER)) {
                final List<SurfaceRules.RuleSource> combined = new ArrayList<>(existingSequence.size() + additionalRules.size());
                for (SurfaceRules.RuleSource rule : existingSequence) {
                    if (rule instanceof SurfaceRules.TestRuleSource testRule
                            && testRule.ifTrue() instanceof SurfaceRules.BiomeConditionSource) {
                        combined.addAll(additionalRules);
                    }
                    combined.add(rule);
                }
                additionalRules = combined;
            } else {
                additionalRules.addAll(existingSequence);
            }
        } else {
            if (!additionalRules.contains(org))
                additionalRules.add(org);
        }

        WoverSurface.C.LOG.verbose(
                "Merged {} additional Surface Rules for {} => {} ({})",
                count,
                source,
                additionalRules.size(),
                sw.stop()
        );

        return new SurfaceRules.SequenceRuleSource(additionalRules);
    }

    @ApiStatus.Internal
    public static void injectNoiseBasedSurfaceRules(
            ResourceKey<LevelStem> dimensionKey,
            Holder<NoiseGeneratorSettings> noiseSettings,
            BiomeSource loadedBiomeSource
    ) {
        Object o = noiseSettings.value();
        if (o instanceof SurfaceRuleProvider srp) {
            SurfaceRules.RuleSource originalRules = srp.wover_getOriginalSurfaceRules();
            srp.wover_overwriteSurfaceRules(mergeSurfaceRules(
                    dimensionKey,
                    originalRules,
                    loadedBiomeSource,
                    getRulesForBiomes(loadedBiomeSource.possibleBiomes().stream().map(Holder::value).toList())
            ));
        }
    }

    static void injectSurfaceRulesToAllDimensions(
            LevelStorageSource.LevelStorageAccess ignoredStorageAccess,
            PackRepository ignoredPackRepository,
            LayeredRegistryAccess<RegistryLayer> registries,
            WorldData ignoredWorldData
    ) {
        final Registry<LevelStem> dimensionRegistry = registries
                .compositeAccess()
                .registryOrThrow(Registries.LEVEL_STEM);

        for (var entry : dimensionRegistry.entrySet()) {
            ResourceKey<LevelStem> dimensionKey = entry.getKey();
            LevelStem stem = entry.getValue();

            if (stem.generator() instanceof InjectableSurfaceRules<?> generator) {
                generator.wover_injectSurfaceRules(dimensionRegistry, dimensionKey);
            }
        }
    }
}
