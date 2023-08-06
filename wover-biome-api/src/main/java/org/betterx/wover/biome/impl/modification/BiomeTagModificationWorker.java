package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.biome.mixin.HolderSetNamedAccessor;
import org.betterx.wover.entrypoint.WoverBiome;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class BiomeTagModificationWorker {
    final Map<HolderSetNamedAccessor<Biome>, List<Holder<Biome>>> unfrozen = new HashMap<>();

    boolean addBiomeToTag(TagKey<Biome> tag, BiomePredicate.Context context) {
        HolderSet.Named<Biome> tagHolder = context.biomes.getOrCreateTag(tag);
        if (tagHolder instanceof HolderSetNamedAccessor<?>) {
            HolderSetNamedAccessor<Biome> biomeTagHolder = (HolderSetNamedAccessor<Biome>) tagHolder;

            if (biomeTagHolder.wover_getContents().stream()
                              .map(Holder::unwrapKey)
                              .filter(Optional::isPresent)
                              .map(Optional::get)
                              .anyMatch(key -> key.equals(context.biomeKey))) {
                return false;
            }

            final List<Holder<Biome>> contents = unfrozen.computeIfAbsent(
                    biomeTagHolder,
                    holder -> new LinkedList<>(biomeTagHolder.wover_getContents())
            );
            contents.add(context.biomeHolder);

            return true;
        } else {
            WoverBiome.C.log.warn("Failed to alter BiomeTag {}", tag.location());
        }
        return false;
    }

    boolean finished() {
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
