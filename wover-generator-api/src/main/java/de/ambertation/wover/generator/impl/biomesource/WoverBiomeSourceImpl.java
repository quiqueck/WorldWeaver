package de.ambertation.wover.generator.impl.biomesource;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeSource;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class WoverBiomeSourceImpl {
    /**
     * Get a list of namespaces from a collection of biomes.
     *
     * @param biomes The collection of biomes.
     * @return A comma-separated list of namespaces including the number of biomes found in each namespace.
     */
    public static String getNamespaces(Collection<Holder<Biome>> biomes) {
        var namespaces = biomes
                .stream()
                .filter(h -> h.unwrapKey().isPresent())
                .map(h -> h.unwrapKey().get().identifier().getNamespace())
                .toList();

        return namespaces
                .stream()
                .distinct()
                .map(n -> n + "(" + namespaces.stream().filter(n::equals).count() + ")")
                .collect(Collectors.joining(", "));

    }

    public record PopulateResult(Set<Holder<Biome>> possibleBiomes, List<WoverBiomeSource.TagToPicker> pickers) {
    }

    public static @Nullable Set<Holder<Biome>> populateBiomePickers(
            List<WoverBiomeSource.TagToPicker> pickers,
            WoverBiomeSource.PickerAdder pickerAdder
    ) {
        RegistryAccess access = WorldState.registryAccess();
        if (access == null) {
            access = WorldState.allStageRegistryAccess();
            if (access != null) {
                LibWoverWorldGenerator.C.log.verbose("Registries were not finalized before populating BiomePickers!");
            } else {
                if (!ModCore.isDatagen()) {
                    LibWoverWorldGenerator.C.log.verbose("Unable to build Biome List yet");
                }
                return null;
            }
        }

        // LinkedHashSet, not HashSet: this set becomes WoverBiomeSource#dynamicPossibleBiomes and thus the
        // stream behind BiomeSource#possibleBiomes(). Vanilla collects that stream into an ImmutableSet, so
        // it faithfully preserves whatever order we hand it - and ChunkGenerator/ChunkGeneratorHelper pass
        // List.copyOf(possibleBiomes()) to FeatureSorter.buildFeaturesPerStep(), whose topological sort
        // breaks ties by first-seen index. Holder.Reference hashes by identity, so a HashSet here made the
        // per-step feature order differ on every boot. Insertion below is driven by the sorted stream, so
        // this order is now a pure function of the Biome set.
        final Set<Holder<Biome>> allBiomes = new LinkedHashSet<>();
        // Only ever queried with contains(), never iterated - hash order is irrelevant here.
        final Set<ResourceKey<Biome>> addedBiomes = new HashSet<>();
        final Registry<Biome> biomes = access.lookupOrThrow(Registries.BIOME);
        final Registry<BiomeData> biomeData = access.lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);

        for (WoverBiomeSource.TagToPicker mapper : pickers) {
            final Optional<HolderSet.Named<Biome>> optionalTag = biomes.get(mapper.tag());
            if (optionalTag.isPresent()) {
                final HolderSet.Named<Biome> tag = optionalTag.get();
                final Set<Identifier> excluded = BiomeSourceManagerImpl.getExcludedBiomes(tag.key());

                tag.stream()
                   .filter(holder -> holder.unwrapKey().isPresent())
                   .map(holder -> new Pair<>(holder, holder.unwrapKey().get()))
                   .filter(pair -> !addedBiomes.contains(pair.second))
                   .filter(pair -> !excluded.contains(pair.second.identifier()))
                   .sorted(Comparator.comparing(pair -> pair.second.identifier().toString()))
                   .forEach(pair -> {
                       final boolean isPossible;
                       final BiomeData data = BiomeDataRegistryImpl.getFromRegistryOrTemp(
                               biomeData,
                               pair.second
                       );

                       if (data != null && data.isPickable()) {
                           isPossible = pickerAdder.add(data, mapper.tag(), mapper.picker());
                       } else {
                           isPossible = true;
                       }

                       if (isPossible) {
                           addedBiomes.add(pair.second);
                           allBiomes.add(pair.first);
                       }
                   });
            }
        }

        return allBiomes;
    }
}
