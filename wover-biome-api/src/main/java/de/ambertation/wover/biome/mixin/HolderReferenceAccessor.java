package de.ambertation.wover.biome.mixin;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * Exposes {@code Holder.Reference}'s own bound-tags set. {@link Holder#is(TagKey)} and
 * {@code HolderSet.Named#contains(Holder)} (which just delegates to {@code is(TagKey)}, see
 * BiomeTagModificationWorker's own doc for the decompiled proof) both read this field, populated once by
 * {@code MappedRegistry.bindTags(...)} during normal datapack tag loading - not the tag's own
 * {@code HolderSet.Named#contents} list, which is the only thing {@link HolderSetNamedAccessor} lets
 * {@code BiomeTagModificationWorker} mutate. A runtime tag addition needs to update both, or the standard
 * way any code (vanilla or a mod) checks tag membership never sees it.
 */
@Mixin(Holder.Reference.class)
public interface HolderReferenceAccessor<T> {
    @Accessor("tags")
    Set<TagKey<T>> wover_getTags();

    @Accessor("tags")
    void wover_setTags(Set<TagKey<T>> tags);
}
