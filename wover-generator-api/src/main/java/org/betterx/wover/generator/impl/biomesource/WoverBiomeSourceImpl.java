package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
                .map(h -> h.unwrapKey().get().location().getNamespace())
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

        final Set<Holder<Biome>> allBiomes = new HashSet<>();
        final Set<ResourceKey<Biome>> addedBiomes = new HashSet<>();
        final Registry<Biome> biomes = access.registryOrThrow(Registries.BIOME);
        final Registry<BiomeData> biomeData = access.registryOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);

        for (WoverBiomeSource.TagToPicker mapper : pickers) {
            final Optional<HolderSet.Named<Biome>> optionalTag = biomes.getTag(mapper.tag());
            if (optionalTag.isPresent()) {
                final HolderSet.Named<Biome> tag = optionalTag.get();
                final Set<ResourceLocation> excluded = BiomeSourceManagerImpl.getExcludedBiomes(tag.key());

                tag.stream()
                   .filter(holder -> holder.unwrapKey().isPresent())
                   .map(holder -> new Pair<>(holder, holder.unwrapKey().get()))
                   .filter(pair -> !addedBiomes.contains(pair.second))
                   .filter(pair -> !excluded.contains(pair.second.location()))
                   .sorted(Comparator.comparing(pair -> pair.second.location().toString()))
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
