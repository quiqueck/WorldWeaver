package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.potions.impl.PotionManagerImpl;
import de.ambertation.wover.recipe.api.OnBootstrapRecipes;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

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
     * This hand-off is safe because it is not a guess about ordering: {@code ReloadableServerResources}'
     * constructor is the only place in the game that constructs either manager (verified against the 26.3
     * jar), and it constructs {@code RecipeManager} first and {@code ServerAdvancementManager} a few
     * instructions later, both from the same {@code LoadResult#lookupWithUpdatedTags()} instance. The
     * provider is carried along and compared by identity so a leftover contribution can never be applied to
     * an unrelated reload.
     */
    private static volatile Contribution pendingAdvancements = null;

    private record Contribution(HolderLookup.Provider registries, List<AdvancementHolder> advancements) {
    }

    /**
     * Adds the recipes contributed at runtime (through {@link #BOOTSTRAP_RECIPES} and through
     * {@link de.ambertation.wover.potions.api.PotionManager#BOOTSTRAP_POTIONS}) to the recipes that were
     * loaded from datapacks, and returns the combined map. The unlock advancements produced along the way are
     * parked for {@link #takeContributedAdvancements(HolderLookup.Provider)}.
     * <p>
     * 26.3 made recipes a datapack registry: {@code RecipeManager} is no longer a reload listener with an
     * {@code apply(RecipeMap, ResourceManager, ProfilerFiller)} phase, it builds its {@link RecipeMap} in its
     * constructor from the {@link HolderLookup.Provider} it is given. That is also why the registry lookups
     * are taken from that provider now instead of from {@code WorldState.registryAccess()} - at construction
     * time the server's registry access is not necessarily published yet, and the constructor's provider is
     * the authoritative view of the world being loaded anyway.
     * <p>
     * The contribution is folded into the lookup on its way into {@code RecipeMap.create} rather than into
     * the finished map afterwards. Building a replacement map and assigning it over
     * {@code RecipeManager.recipes} would drop Fabric's recipe-sync decoration and break player connect -
     * see {@link RecipeLookups} for the full account.
     *
     * @param loaded     the lookup over the recipes loaded from datapacks
     * @param registries the registry view the recipes are resolved against
     * @return {@code loaded} when nothing was contributed, otherwise a view holding both
     */
    @ApiStatus.Internal
    public static HolderLookup<Recipe<?>> loadedRecipes(
            HolderLookup<Recipe<?>> loaded,
            HolderLookup.Provider registries
    ) {
        final RuntimeRecipeOutput output = new RuntimeRecipeOutput(registries);

        final RecipeBuilder.Context builderContext = output.newBuilderContext();
        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(builderContext));

        // Brewing is part of the recipe system in 26.3, so BOOTSTRAP_POTIONS contributes here too.
        PotionManagerImpl.bootstrapBrewingRecipes(output);

        // Published unconditionally (possibly empty) so a previous world's contribution can never leak into
        // this one's ServerAdvancementManager.
        pendingAdvancements = new Contribution(registries, List.copyOf(output.advancements()));

        // Contributed recipes are keyed, so a contributor can deliberately replace a datapack recipe and two
        // contributors cannot blow up the ImmutableMap builder with a duplicate key.
        return RecipeLookups.extended(loaded, output.recipes());
    }

    /**
     * Consumes the unlock advancements produced by the {@link #loadedRecipes} run that belongs to
     * {@code registries}. Returns an empty list when this load contributed nothing, or when no matching
     * contribution is parked (which would mean a {@code ServerAdvancementManager} was built without a
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
