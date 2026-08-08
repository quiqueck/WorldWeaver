package de.ambertation.wover.test.api.gametest;

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
 * produce" accessor - the only way left to ask is {@code Recipe#assemble(RecipeInput)}, which needs a
 * real, type-matching input. Most recipe types this codebase uses (shaped/shapeless crafting, smithing
 * upgrades, anvil recipes) ignore their input entirely and just return a fixed result -
 * {@code AnvilCategory.java}'s own JEI display already relies on exactly that, calling
 * {@code recipe.assemble(null)} to get a preview with no real input in hand. This sweep does the same,
 * and treats any recipe whose {@code assemble(null)} throws as "couldn't determine", silently skipping
 * it rather than crashing the whole sweep - a handful of unresolvable recipes should not hide every
 * other, real gap.
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
            if (!key.identifier().getNamespace().equals(namespace)) continue;

            final Item item = entry.getValue();
            if (item == Items.AIR) continue;
            if (!include.test(item)) continue;

            if (!produced.contains(item)) {
                missing.add(key.identifier().toString());
            }
        }
        return missing;
    }

    /** Every item any currently-loaded recipe can be made to produce. */
    private static Set<Item> recipeResults(ServerLevel level) {
        final Set<Item> results = new HashSet<>();
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            final ItemStack out = tryAssemble(holder.value());
            if (out != null && !out.isEmpty()) {
                results.add(out.getItem());
            }
        }
        return results;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ItemStack tryAssemble(Recipe recipe) {
        try {
            return (ItemStack) recipe.assemble(null);
        } catch (Exception ignored) {
            // Fall through - smithing recipes NPE on a null RecipeInput (they read template/base/
            // addition off it directly), unlike crafting/blasting/alloying/anvil recipes which mostly
            // ignore their input. Confirmed empirically: without this, every item this codebase only
            // ships via ToolsWithHeadsSet's generic smithing-upgrade registration (every metal-tier
            // sword/pickaxe/axe/shovel/hoe/armor piece, not just the hand-listed entries in
            // SmithingRecipesProvider) was reported as "missing a recipe" when it plainly has one.
        }
        try {
            return (ItemStack) recipe.assemble(new SmithingRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
        } catch (Exception ignored) {
            // A recipe type that genuinely needs its input to compute a result (e.g. one that copies
            // data components across) - not a coverage gap, just not answerable this way.
            return null;
        }
    }
}
