package de.ambertation.wover.test.api.gametest;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generalizes the registry-sweep pattern {@code ObsidianBreakerGameTest.everyObsidianBlockIsCoveredByTheTag}
 * established: walk every item in a namespace and report which ones no known recipe actually produces,
 * rather than hand-listing "the items that should have a recipe" up front. That is the mechanism that
 * finds a gap like "no hammer exists for the terminite/thallasium/aeternium tiers" automatically -
 * hand-enumerating would just repeat the same omission the code itself has.
 * <p>
 * <b>Best-effort, not exhaustive.</b> Modern {@code Recipe<T>} dropped a context-free "what does this
 * produce" accessor - the only way left to ask is {@code Recipe#assemble(RecipeInput, HolderLookup.Provider)},
 * which needs a real, type-matching input. Most recipe types this codebase uses (shaped/shapeless
 * crafting, smithing upgrades, anvil recipes) ignore their input entirely and just return a fixed
 * result - {@code AnvilCategory.java}'s own JEI display already relies on exactly that, calling
 * {@code recipe.assemble(null, provider)} to get a preview with no real input in hand. This sweep does
 * the same, and treats any recipe whose {@code assemble(null, provider)} throws as "couldn't
 * determine", silently skipping it rather than crashing the whole sweep - a handful of unresolvable
 * recipes should not hide every other, real gap.
 */
public final class RecipeCoverageSweep {
    private RecipeCoverageSweep() {}

    /** Returns the {@code namespace:path} id of every registered item in {@code namespace} that no
     *  recipe {@code assemble()}s to, excluding {@code minecraft:air}. */
    public static List<String> findItemsMissingARecipe(GameTestHelper helper, String namespace) {
        return findItemsMissingARecipe(helper, namespace, item -> true);
    }

    /**
     * As {@link #findItemsMissingARecipe(GameTestHelper, String)}, but only considers items accepted by
     * {@code include} - e.g. restrict the sweep to tool/armor items rather than every block-item too.
     */
    public static List<String> findItemsMissingARecipe(
            GameTestHelper helper,
            String namespace,
            java.util.function.Predicate<Item> include
    ) {
        final Set<Item> produced = recipeResults(helper.getLevel());

        final List<String> missing = new ArrayList<>();
        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            final ResourceKey<Item> key = entry.getKey();
            if (!key.location().getNamespace().equals(namespace)) continue;

            final Item item = entry.getValue();
            if (item == Items.AIR) continue;
            if (!include.test(item)) continue;

            if (!produced.contains(item)) {
                missing.add(key.location().toString());
            }
        }
        return missing;
    }

    /** Every item any currently-loaded recipe can be made to produce. */
    private static Set<Item> recipeResults(ServerLevel level) {
        final Set<Item> results = new HashSet<>();
        final HolderLookup.Provider registries = level.registryAccess();
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            final ItemStack out = tryAssemble(holder.value(), registries);
            if (out != null && !out.isEmpty()) {
                results.add(out.getItem());
            }
        }
        return results;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ItemStack tryAssemble(Recipe recipe, HolderLookup.Provider registries) {
        try {
            return (ItemStack) recipe.assemble(null, registries);
        } catch (Exception ignored) {
            // Fall through - smithing recipes NPE on a null RecipeInput (they read template/base/
            // addition off it directly), unlike crafting/blasting/alloying/anvil recipes which mostly
            // ignore their input. Confirmed empirically: without this, every item this codebase only
            // ships via ToolsWithHeadsSet's generic smithing-upgrade registration (every metal-tier
            // sword/pickaxe/axe/shovel/hoe/armor piece, not just the hand-listed entries in
            // SmithingRecipesProvider) was reported as "missing a recipe" when it plainly has one.
        }
        try {
            // The base slot has to be a non-empty stack here, unlike template/addition: this branch's
            // SmithingTransformRecipe#assemble is `result.apply(input.base())`, and
            // ItemStack#transmuteCopy short-circuits to ItemStack.EMPTY whenever the stack it's called
            // on isEmpty() - so an all-EMPTY SmithingRecipeInput silently "succeeds" with an empty
            // result for every smithing_transform recipe rather than throwing, which would make every
            // item this codebase ships only through a generic smithing-upgrade registration (e.g.
            // ToolsWithHeadsSet's tiered tools/armor) misreport as missing a recipe. A plain vanilla
            // stick carries no mod-specific data components, so it is a safe generic stand-in for any
            // smithing_transform recipe's base ingredient regardless of which mod defines it.
            return (ItemStack) recipe.assemble(
                    new SmithingRecipeInput(ItemStack.EMPTY, new ItemStack(Items.STICK), ItemStack.EMPTY), registries
            );
        } catch (Exception ignored) {
            // A recipe type that genuinely needs its input to compute a result (e.g. one that copies
            // data components across) - not a coverage gap, just not answerable this way.
            return null;
        }
    }
}
