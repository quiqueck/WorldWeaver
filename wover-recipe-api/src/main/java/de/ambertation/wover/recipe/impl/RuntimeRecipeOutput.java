package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.recipe.api.RecipeBuilder;

import com.mojang.serialization.Lifecycle;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BootstrapRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The runtime implementation of 26.3's {@link RecipeOutput} contract: it collects both halves of what a
 * recipe produces - the {@link Recipe} itself and the {@code minecraft:recipe_unlocked} advancement that
 * unlocks it in the recipe book - so that {@link RecipeRuntimeProviderImpl} can hand the first to
 * {@code RecipeManager} and the second to {@code ServerAdvancementManager}.
 * <p>
 * <b>Why both halves have to be collected.</b> {@code RecipeOutput#accept} has always taken the unlock
 * advancement as its third argument, and every vanilla recipe builder produces one (through
 * {@code RecipeUnlockAdvancementBuilder}). Up to 26.1 WoVer dropped that argument on the runtime path, so a
 * recipe contributed through {@code RecipeBuilder#BOOTSTRAP_RECIPES} never unlocked itself in the recipe
 * book: the player had to be given it with {@code /recipe give}. That was a long-standing bug, not a
 * property of the API. 26.3 makes it fixable because advancements became a datapack registry with exactly
 * the same lifecycle as recipes - {@code ServerAdvancementManager} is built from a
 * {@link HolderLookup.Provider} in its constructor, just like {@code RecipeManager} - so the advancement
 * half has the same injection point the recipe half already had.
 * <p>
 * <b>About the recipe holders.</b> {@code RecipeUnlockAdvancementBuilder} resolves the recipe it unlocks
 * through {@code lookup(Registries.RECIPE).getOrThrow(key)}. Recipes contributed here are by definition not
 * in the (already frozen) {@code minecraft:recipe} registry, so {@link #lookup} answers from the real
 * registry first and falls back to a {@link BootstrapRegistry}, which mints a stand-alone, value-less
 * {@link Holder.Reference} for any key on demand - the same device Fabric's data generator uses. That is
 * enough, and provably so: the only thing that is ever asked of the holder is
 * {@code Holder#is(ResourceKey)}, which {@code Holder.Reference} answers from its key alone
 * ({@code RecipeUnlockedTrigger.TriggerInstance#matches} does
 * {@code recipes.stream().anyMatch(h -> h.is(recipeHolder.id()))}), and {@code Advancement.STREAM_CODEC}
 * transmits only parent/display/requirements - criteria never leave the server. This is why wunderreich's
 * conclusion that a runtime-only recipe "has no registry holder to reference" does not force the criterion
 * to be dropped: it has no <em>bound</em> holder, but the criterion never needs one.
 */
@ApiStatus.Internal
public final class RuntimeRecipeOutput implements RecipeOutput {
    private final HolderLookup.Provider registries;
    private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipes = new LinkedHashMap<>();
    private final Map<Identifier, AdvancementHolder> advancements = new LinkedHashMap<>();

    /**
     * Mints stand-alone {@link Holder.Reference}s for the recipes contributed here, which cannot be in the
     * frozen {@code minecraft:recipe} registry. See the class javadoc for why an unbound reference is
     * sufficient.
     */
    private final BootstrapRegistry<Recipe<?>> contributedHolders =
            new BootstrapRegistry<>(Registries.RECIPE, Lifecycle.stable());

    /**
     * Creates an output that resolves its ingredients against {@code registries}.
     *
     * @param registries the registry view the contributed recipes are resolved against; on the runtime path
     *                   this is the provider {@code RecipeManager}'s constructor was given
     */
    public RuntimeRecipeOutput(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    /**
     * Creates the {@link RecipeBuilder.Context} that WoVer's own builders are driven with. Unlike the public
     * {@link RecipeBuilder.Context#Context(HolderLookup.Provider, RecipeOutput)} constructor - which can only
     * see a bare {@link RecipeOutput} - this wires the helper provider's advancement channel back into this
     * output, so advancements registered directly on {@code RecipeProvider#advancementOutput} are kept too.
     *
     * @return the context to hand to {@link de.ambertation.wover.recipe.api.OnBootstrapRecipes} subscribers
     */
    public RecipeBuilder.Context newBuilderContext() {
        return new RecipeBuilder.Context(
                registries,
                RecipeProviderAdapter.providerFor(this, this::acceptAdvancement),
                this
        );
    }

    @Override
    public void accept(
            ResourceKey<Recipe<?>> key,
            Recipe<?> recipe,
            @Nullable AdvancementHolder advancement
    ) {
        recipes.put(key, new RecipeHolder<>(key, recipe));
        if (advancement != null) acceptAdvancement(advancement);
    }

    /**
     * Records an advancement produced alongside a recipe. First writer wins, mirroring how the recipe half
     * cannot produce two advancements for the same id either.
     *
     * @param advancement the advancement to record
     */
    public void acceptAdvancement(AdvancementHolder advancement) {
        advancements.putIfAbsent(advancement.id(), advancement);
    }

    /**
     * @return the recipes contributed so far, in contribution order
     */
    public Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipes() {
        return recipes;
    }

    /**
     * @return the unlock advancements contributed so far, in contribution order
     */
    public Collection<AdvancementHolder> advancements() {
        return advancements.values();
    }

    // Mirrors RecipeProvider$1#advancement() exactly: a parentless recipe advancement hung under
    // minecraft:recipes/root, which vanilla's own datapack always ships.
    @Override
    @SuppressWarnings("removal")
    public Advancement.@NotNull Builder advancement() {
        return Advancement.Builder
                .recipeAdvancement()
                .parent(net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
    }

    // 26.3: RecipeOutput extends BootstrapContextAccess, so it has to answer registry lookups.
    @Override
    @SuppressWarnings("unchecked")
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
        if (Registries.RECIPE.equals(key)) {
            return (HolderGetter<S>) (HolderGetter<?>) recipeLookup();
        }
        return registries.lookupOrThrow(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
        if (Registries.RECIPE.equals(key)) {
            return (Stream<Holder.Reference<S>>) (Stream<?>) Stream.concat(
                    registries.lookupOrThrow(Registries.RECIPE).listElements(),
                    contributedHolders.listElements()
            );
        }
        return registries.lookupOrThrow(key).listElements();
    }

    /**
     * The {@code minecraft:recipe} view this output exposes: the world's real recipe registry, with the
     * stand-alone holders for our own contributions behind it. Preferring the real registry matters - a
     * recipe contributed here that unlocks alongside a datapack recipe then gets a properly bound holder
     * rather than a synthetic one.
     */
    private HolderGetter<Recipe<?>> recipeLookup() {
        final HolderLookup.RegistryLookup<Recipe<?>> real = registries.lookupOrThrow(Registries.RECIPE);
        return new HolderGetter<>() {
            @Override
            public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key) {
                final Optional<Holder.Reference<Recipe<?>>> loaded = real.get(key);
                return loaded.isPresent() ? loaded : contributedHolders.get(key);
            }

            @Override
            public Optional<HolderSet.Named<Recipe<?>>> get(TagKey<Recipe<?>> tag) {
                // Recipes carry no tags in 26.3; forwarding keeps the contract honest if that ever changes.
                return real.get(tag);
            }
        };
    }
}
