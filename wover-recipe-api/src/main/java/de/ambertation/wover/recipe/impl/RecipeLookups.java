package de.ambertation.wover.recipe.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;

/**
 * Views over the recipe {@link HolderLookup} that {@code RecipeManager}'s constructor hands to
 * {@code RecipeMap.create}, for adding and removing recipes before the map is built.
 * <p>
 * The alternative - letting the map be built and then replacing {@code RecipeManager.recipes} with a
 * rebuilt one - is what this exists to avoid. Fabric's recipe synchronisation decorates the map on its
 * way out of {@code RecipeMap.create} (a {@code @ModifyReturnValue} that attaches a
 * serializer-to-recipes index used when a player connects). A map built any other way carries no index
 * at all, so swapping the field left every joining player hitting a null map inside
 * {@code RecipeSyncImpl#sendRecipes} - kicked with "Invalid player data", server down - the moment any
 * mod registered a synced recipe serializer. Feeding the lookup instead means {@code create} runs once
 * over the final set and its result is the map that gets kept, decoration and all.
 */
@ApiStatus.Internal
public final class RecipeLookups {
    private RecipeLookups() {
    }

    /**
     * {@code base} plus {@code extra}, with a contributed recipe replacing a datapack recipe of the same
     * key rather than colliding with it - {@code RecipeMap.create} collects into an
     * {@link com.google.common.collect.ImmutableMap.Builder}, which throws on a duplicate key.
     */
    public static HolderLookup<Recipe<?>> extended(
            HolderLookup<Recipe<?>> base,
            Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> extra
    ) {
        if (extra.isEmpty()) return base;

        final Map<ResourceKey<Recipe<?>>, Holder.Reference<Recipe<?>>> added = new LinkedHashMap<>();
        extra.forEach((key, holder) -> added.put(key, reference(base, key, holder)));

        return new Delegating(base) {
            @Override
            public Stream<Holder.Reference<Recipe<?>>> listElements() {
                return Stream.concat(
                        base.listElements().filter(ref -> !added.containsKey(ref.key())),
                        added.values().stream()
                );
            }

            @Override
            public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key) {
                Holder.Reference<Recipe<?>> ours = added.get(key);
                return ours != null ? Optional.of(ours) : base.get(key);
            }
        };
    }

    /**
     * {@code base} without the recipes {@code keep} rejects.
     */
    public static HolderLookup<Recipe<?>> filtered(
            HolderLookup<Recipe<?>> base,
            Predicate<ResourceKey<Recipe<?>>> keep
    ) {
        return new Delegating(base) {
            @Override
            public Stream<Holder.Reference<Recipe<?>>> listElements() {
                return base.listElements().filter(ref -> keep.test(ref.key()));
            }

            @Override
            public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key) {
                return keep.test(key) ? base.get(key) : Optional.empty();
            }
        };
    }

    /**
     * A standalone {@link Holder.Reference} for a recipe that has no registry entry of its own.
     * <p>
     * Owned by {@code base} rather than by a fresh owner, so {@code canSerializeIn} answers the same as it
     * would for a datapack recipe. {@code bindValue} is {@code protected}, hence the access widener.
     */
    private static Holder.Reference<Recipe<?>> reference(
            HolderLookup<Recipe<?>> base,
            ResourceKey<Recipe<?>> key,
            RecipeHolder<?> holder
    ) {
        Holder.Reference<Recipe<?>> reference = Holder.Reference.createStandAlone(base, key);
        reference.bindValue(holder.value());
        return reference;
    }

    /**
     * Everything except {@code listElements}/{@code get(ResourceKey)} passes straight through - recipes
     * carry no tags, so the tag half is never anything but the delegate's.
     */
    private abstract static class Delegating implements HolderLookup<Recipe<?>> {
        private final HolderLookup<Recipe<?>> base;

        private Delegating(HolderLookup<Recipe<?>> base) {
            this.base = base;
        }

        @Override
        public Stream<HolderSet.Named<Recipe<?>>> listTags() {
            return this.base.listTags();
        }

        @Override
        public Optional<HolderSet.Named<Recipe<?>>> get(TagKey<Recipe<?>> tag) {
            return this.base.get(tag);
        }
    }
}
