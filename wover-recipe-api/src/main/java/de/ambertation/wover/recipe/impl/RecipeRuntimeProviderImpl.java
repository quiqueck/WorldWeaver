package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.recipe.api.OnBootstrapRecipes;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeRuntimeProviderImpl {
    public static final EventImpl<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            new EventImpl<>("BOOTSTRAP_RECIPES");

    public record LoadedRecipes(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
                                Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byName) {
    }

    /**
     * The unlock advancements produced by the most recent {@link #loadedRecipes} run, waiting to be picked up
     * by {@code ServerAdvancementManagerMixin}.
     * <p>
     * This hand-off relies on {@code RecipeManager} and {@code ServerAdvancementManager} being applied in that
     * order during the same datapack reload, which is not a guess: {@code ReloadableServerResources#listeners()}
     * returns {@code List.of(recipes, functionLibrary, advancements)}, and
     * {@code SimpleReloadInstance#prepareTasks} chains every listener's preparation barrier onto the previous
     * listener's completed future, so listener N's {@code apply} strictly happens-before listener N+1's.
     * Fabric does not disturb this - {@code ResourceManagerHelperImpl#sort} never reorders vanilla listeners
     * and only appends modded ones.
     * <p>
     * The {@link HolderLookup.Provider} both managers were constructed with is carried along and compared by
     * identity, so a leftover contribution can never be applied to an unrelated reload. It is the same instance
     * for both: {@code ReloadableServerResources}' constructor is the only place either manager is built and
     * passes its own provider argument to both.
     */
    private static volatile Contribution pendingAdvancements = null;

    private record Contribution(HolderLookup.Provider registries, List<AdvancementHolder> advancements) {
    }

    /**
     * Adds the recipes contributed at runtime (through {@link #BOOTSTRAP_RECIPES}) to the recipes that were
     * loaded from datapacks, and returns the combined map. The unlock advancements produced along the way are
     * parked for {@link #takeContributedAdvancements(HolderLookup.Provider)}.
     *
     * @param loaded     the recipes the datapacks provided
     * @param registries the provider the calling {@code RecipeManager} was constructed with
     * @return the combined recipe map, or {@code loaded} itself when nothing was contributed
     */
    @ApiStatus.Internal
    public static RecipeMap loadedRecipes(
            RecipeMap loaded,
            HolderLookup.Provider registries
    ) {
        final boolean[] didInit = {false};
        List<RecipeHolder<?>> recipeHolders = new LinkedList<>();
        // The unlock advancement every vanilla recipe builder produces. Up to now it was dropped here, so a
        // recipe contributed through BOOTSTRAP_RECIPES never got a minecraft:recipe_unlocked advancement and
        // never entered the recipe book - the player had to be handed it with `/recipe give`. That was a bug,
        // not a property of the API.
        List<AdvancementHolder> advancements = new ArrayList<>();

        RecipeOutput recipeOutput = new RecipeOutput() {
            @Override
            public void accept(
                    ResourceKey<Recipe<?>> resourceKey,
                    Recipe<?> recipe,
                    @Nullable AdvancementHolder advancementHolder
            ) {
                if (!didInit[0]) {
                    recipeHolders.addAll(loaded.values());
                    didInit[0] = true;
                }
                RecipeHolder<?> recipeHolder = new RecipeHolder<>(resourceKey, recipe);
                recipeHolders.add(recipeHolder);

                if (advancementHolder != null) advancements.add(advancementHolder);
            }

            @Override
            @SuppressWarnings("removal")
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder
                        .recipeAdvancement()
                        .parent(net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void includeRootAdvancement() {
                //this is created by vanilla code, so we don't need to do anything here
            }

        };


        final var builderContext = new RecipeBuilder.Context(
                WorldState.registryAccess(), recipeOutput
        );
        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(builderContext));

        // Published unconditionally (possibly empty) so a previous world's contribution can never leak into
        // this one's ServerAdvancementManager.
        pendingAdvancements = new Contribution(registries, List.copyOf(advancements));

        if (!didInit[0]) return loaded;
        // Must stay RecipeMap.create: Fabric's recipe synchronisation attaches its serializer index to
        // whatever that factory returns, and the map handed back here is the one RecipeManager keeps. A map
        // built any other way (its own constructor, some future builder) carries a null index, and every
        // joining player then dies inside RecipeSyncImpl#sendRecipes with nothing pointing back to here.
        // 26.3 lost this overload and has to fold the contribution into the lookup instead - see the
        // RecipeLookups class on that branch.
        return RecipeMap.create(recipeHolders);
    }

    /**
     * Consumes the unlock advancements produced by the {@link #loadedRecipes} run that belongs to
     * {@code registries}. Returns an empty list when this load contributed nothing, or when no matching
     * contribution is parked (which would mean a {@code ServerAdvancementManager} was applied without a
     * {@code RecipeManager} in front of it - impossible in vanilla, but not worth crashing over).
     *
     * @param registries the provider the calling {@code ServerAdvancementManager} was constructed with
     * @return the advancements to add, in contribution order
     */
    @ApiStatus.Internal
    public static List<AdvancementHolder> takeContributedAdvancements(HolderLookup.Provider registries) {
        final Contribution contribution = pendingAdvancements;
        pendingAdvancements = null;
        if (contribution == null || contribution.registries() != registries) return List.of();
        return contribution.advancements();
    }
}
