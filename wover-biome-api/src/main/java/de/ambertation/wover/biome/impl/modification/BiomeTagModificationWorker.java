package de.ambertation.wover.biome.impl.modification;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.biome.mixin.HolderReferenceAccessor;
import de.ambertation.wover.biome.mixin.HolderSetNamedAccessor;
import de.ambertation.wover.entrypoint.LibWoverBiome;

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
        HolderSet.Named<Biome> tagHolder = biomes.getOrThrow(tag);
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
            unfrozen.forEach((tagHolder, contents) -> {
                final TagKey<Biome> tagKey = ((HolderSet.Named<Biome>) tagHolder).key();
                tagHolder.wover_setContents(List.copyOf(contents));

                // HolderSet.Named#contains(Holder) delegates straight to Holder#is(TagKey) (decompiled
                // and confirmed - it never actually looks at its own `contents` list), which reads the
                // biome's OWN bound-tags set, populated once by MappedRegistry.bindTags(...) during
                // normal datapack tag loading. Mutating only the tag's contents list above (the only
                // thing HolderSetNamedAccessor exposes) makes the tag "contain" the biome for anyone
                // iterating/streaming it, but invisible to the standard membership check every real
                // consumer - vanilla or another mod - actually uses. Every biome in `contents` (not just
                // the ones newly added this pass) gets its own bound set re-synced here so the two never
                // drift apart again.
                for (Holder<Biome> biomeHolder : contents) {
                    if (!(biomeHolder instanceof HolderReferenceAccessor<?> accessor)) continue;
                    @SuppressWarnings("unchecked")
                    HolderReferenceAccessor<Biome> biomeAccessor = (HolderReferenceAccessor<Biome>) accessor;
                    final Set<TagKey<Biome>> existing = biomeAccessor.wover_getTags();
                    final Set<TagKey<Biome>> updated = existing == null ? new HashSet<>() : new HashSet<>(existing);
                    updated.add(tagKey);
                    biomeAccessor.wover_setTags(Set.copyOf(updated));
                }
            });
            unfrozen.clear();
            return true;
        }
        return false;
    }
}
