package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.mixin.HolderSetNamedAccessor;
import org.betterx.wover.entrypoint.LibWoverBiome;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class BiomeTagModificationWorker {
    final Map<HolderSetNamedAccessor<Biome>, List<Holder<Biome>>> unfrozen = new HashMap<>();

    boolean addBiomeToTag(TagKey<Biome> tag, BiomePredicate.Context context) {
        return addBiomeToTag(tag, context.biomes, context.biomeKey, context.biomeHolder);
    }

    public boolean addBiomeToTag(
            TagKey<Biome> tag,
            Registry<Biome> biomes,
            ResourceKey<Biome> biomeKey,
            Holder<Biome> biomeHolder
    ) {
        HolderSet.Named<Biome> tagHolder = biomes.getOrCreateTag(tag);
        if (tagHolder instanceof HolderSetNamedAccessor<?>) {
            HolderSetNamedAccessor<Biome> biomeTagHolder = (HolderSetNamedAccessor<Biome>) tagHolder;

            if (biomeTagHolder.wover_getContents().stream()
                              .map(Holder::unwrapKey)
                              .filter(Optional::isPresent)
                              .map(Optional::get)
                              .anyMatch(key -> key.equals(biomeKey))) {
                return false;
            }

            final List<Holder<Biome>> contents = unfrozen.computeIfAbsent(
                    biomeTagHolder,
                    holder -> new LinkedList<>(biomeTagHolder.wover_getContents())
            );
            contents.add(biomeHolder);

            return true;
        } else {
            LibWoverBiome.C.log.warn("Failed to alter BiomeTag {}", tag.location());
        }
        return false;
    }

    public boolean finished() {
        if (!unfrozen.isEmpty()) {
            unfrozen.forEach((tag, contents) -> {
                tag.wover_setContents(List.copyOf(contents));
            });
            unfrozen.clear();
            return true;
        }
        return false;
    }
}
