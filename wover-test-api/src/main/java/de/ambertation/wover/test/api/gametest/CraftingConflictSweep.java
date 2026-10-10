package de.ambertation.wover.test.api.gametest;

import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finds crafting recipes that can never be crafted at a crafting table because another recipe with a
 * <i>different</i> result claims every grid they accept. Vanilla's crafting menu takes the first
 * matching recipe, and recipes are ordered by id <b>path first</b> ({@code Identifier#compareTo}), so
 * {@code betternether:cincinnasite_bars} is tried before {@code minecraft:iron_bars} - if their grids
 * overlapped, iron bars would become uncraftable (#188), or the mod's own recipe would.
 * <p>
 * For every shaped/shapeless recipe in {@code namespace}, every other shaped/shapeless crafting recipe
 * is compared <b>structurally</b>: same grid size (incl. the mirrored layout) or same ingredient count,
 * with every slot's ingredients sharing at least one item - so an overlap that only exists through a
 * tag (e.g. a mod item inside {@code c:ingots/iron}) is found, not just literal duplicates. Each hit is
 * confirmed by building that shared grid and asking both recipes' own {@code matches()}.
 * <p>
 * An overlap alone is not a bug: a wood-specific chest recipe deliberately overlaps the generic
 * {@code #planks} chest. So both sides of every confirmed overlap with different results - and every
 * recipe in {@code namespace}, which also catches special recipes (dyeing, repair, ...) that have no
 * inspectable shape - are then checked for <b>reachability</b>: some grid the recipe accepts must make
 * the real {@code RecipeManager} return a recipe with the same result. Only unreachable recipes are
 * reported.
 */
public final class CraftingConflictSweep {
    private static final int MAX_CANDIDATES = 64;

    private CraftingConflictSweep() {}

    /** One line per uncraftable recipe, empty if there is none. */
    public static List<String> findConflicts(GameTestHelper helper, String namespace) {
        final ServerLevel level = helper.getLevel();
        final List<Entry> all = new ArrayList<>();
        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (holder.value().getType() != RecipeType.CRAFTING) continue;
            final Entry e = Entry.of(holder);
            if (e != null) all.add(e);
        }

        final Map<Entry, CraftingInput> toCheck = new LinkedHashMap<>();
        for (Entry own : all) {
            if (!inNamespace(own.holder, namespace)) continue;
            toCheck.putIfAbsent(own, null);
            for (Entry other : all) {
                if (other == own) continue;
                final CraftingInput witness = own.overlap(other);
                if (witness == null) continue;
                if (!own.recipe().matches(witness, level) || !other.recipe().matches(witness, level)) continue;
                final ItemStack ra = assemble(own.recipe(), witness);
                final ItemStack rb = assemble(other.recipe(), witness);
                if (ra != null && rb != null && ItemStack.isSameItem(ra, rb)) continue;
                toCheck.putIfAbsent(other, witness);
                if (toCheck.get(own) == null) toCheck.put(own, witness);
            }
        }

        final List<String> problems = new ArrayList<>();
        for (Map.Entry<Entry, CraftingInput> check : toCheck.entrySet()) {
            final Entry entry = check.getKey();
            if (entry.reachable(level)) continue;
            CraftingInput shown = check.getValue() != null ? check.getValue() : entry.candidate(0);
            final String winner = shown == null ? "?" : level.recipeAccess()
                    .getRecipeFor(RecipeType.CRAFTING, shown, level)
                    .map(h -> h.id().identifier() + " (-> " + describe(assemble(h.value(), shown)) + ")")
                    .orElse("nothing");
            problems.add(entry.holder.id().identifier() + " (-> " + describe(entry.result()) + ") is never "
                    + "crafted: e.g. " + (shown == null ? "?" : describe(shown)) + " makes " + winner);
        }
        return problems;
    }

    private static boolean inNamespace(RecipeHolder<?> holder, String namespace) {
        return holder.id().identifier().getNamespace().equals(namespace);
    }

    private static ItemStack assemble(CraftingRecipe recipe, CraftingInput input) {
        try {
            return recipe.assemble(input);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static String describe(ItemStack stack) {
        if (stack == null) return "?";
        return stack.getCount() + "x " + stack.typeHolder().getRegisteredName();
    }

    private static String describe(CraftingInput input) {
        final StringBuilder sb = new StringBuilder("[");
        for (int y = 0; y < input.height(); y++) {
            if (y > 0) sb.append(" / ");
            for (int x = 0; x < input.width(); x++) {
                if (x > 0) sb.append(", ");
                final ItemStack s = input.getItem(x, y);
                sb.append(s.isEmpty() ? "_" : s.typeHolder().getRegisteredName());
            }
        }
        return sb.append("]").toString();
    }

    private static Set<Item> items(Ingredient ingredient) {
        return ingredient.items().map(Holder::value).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** The structure of a shaped (width/height &gt; 0) or shapeless (width == 0) crafting recipe. */
    private record Entry(RecipeHolder<?> holder, int width, int height, List<Set<Item>> slots) {
        static Entry of(RecipeHolder<?> holder) {
            if (holder.value() instanceof ShapedRecipe shaped) {
                final List<Set<Item>> slots = new ArrayList<>();
                for (Optional<Ingredient> i : shaped.getIngredients()) slots.add(i.map(CraftingConflictSweep::items).orElse(null));
                return new Entry(holder, shaped.getWidth(), shaped.getHeight(), slots);
            }
            if (holder.value() instanceof ShapelessRecipe shapeless) {
                final List<Set<Item>> slots = new ArrayList<>();
                for (Ingredient i : shapeless.placementInfo().ingredients()) slots.add(items(i));
                return new Entry(holder, 0, 0, slots);
            }
            return null;
        }

        CraftingRecipe recipe() {
            return (CraftingRecipe) holder.value();
        }

        boolean shaped() {
            return width > 0;
        }

        List<Set<Item>> filled() {
            return slots.stream().filter(s -> s != null).toList();
        }

        /** A grid both recipes structurally accept, or {@code null}. */
        CraftingInput overlap(Entry other) {
            if (shaped() && other.shaped()) {
                if (width != other.width || height != other.height) return null;
                CraftingInput r = shapedOverlap(other, false);
                return r != null ? r : shapedOverlap(other, true);
            }
            final List<Set<Item>> mine = filled();
            final List<Set<Item>> theirs = other.filled();
            if (mine.size() != theirs.size()) return null;
            final int[] match = perfectMatching(mine, theirs);
            if (match == null) return null;
            final Item[] chosen = new Item[mine.size()];
            for (int i = 0; i < mine.size(); i++) chosen[i] = intersect(mine.get(i), theirs.get(match[i]));
            // lay the items out in this recipe's shape if it has one, else the other's, else row-major
            final Entry layout = shaped() ? this : other.shaped() ? other : null;
            if (layout == other) {
                // chosen[] is indexed by our slots; re-index by the other recipe's filled slots
                final Item[] reindexed = new Item[chosen.length];
                for (int i = 0; i < chosen.length; i++) reindexed[match[i]] = chosen[i];
                return grid(layout, reindexed);
            }
            return grid(layout, chosen);
        }

        private CraftingInput shapedOverlap(Entry other, boolean mirror) {
            final Item[] grid = new Item[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final Set<Item> a = slots.get(x + y * width);
                    final Set<Item> b = other.slots.get((mirror ? width - 1 - x : x) + y * width);
                    if ((a == null) != (b == null)) return null;
                    if (a == null) continue;
                    final Item i = intersect(a, b);
                    if (i == null) return null;
                    grid[x + y * width] = i;
                }
            }
            return CraftingInput.of(width, height, Arrays.stream(grid).map(CraftingConflictSweep::stack).toList());
        }

        ItemStack result() {
            final CraftingInput c = candidate(0);
            return c == null ? null : assemble(recipe(), c);
        }

        /** The {@code k}-th grid this recipe accepts: item {@code k} (wrapping) of every slot's ingredient. */
        CraftingInput candidate(int k) {
            final List<Set<Item>> filled = filled();
            final Item[] chosen = new Item[filled.size()];
            for (int i = 0; i < chosen.length; i++) {
                final Set<Item> slot = filled.get(i);
                if (slot.isEmpty()) return null;
                chosen[i] = slot.stream().skip(k % slot.size()).findFirst().orElseThrow();
            }
            return grid(shaped() ? this : null, chosen);
        }

        /** Whether some grid this recipe accepts makes the recipe manager produce this recipe's result. */
        boolean reachable(ServerLevel level) {
            final int widest = filled().stream().mapToInt(Set::size).max().orElse(1);
            for (int k = 0; k < Math.min(widest, MAX_CANDIDATES); k++) {
                final CraftingInput input = candidate(k);
                if (input == null || !recipe().matches(input, level)) continue;
                final ItemStack want = assemble(recipe(), input);
                final ItemStack got = level.recipeAccess()
                                           .getRecipeFor(RecipeType.CRAFTING, input, level)
                                           .map(h -> assemble(h.value(), input))
                                           .orElse(null);
                if (want != null && got != null && ItemStack.isSameItem(want, got)) return true;
            }
            return false;
        }
    }

    /** Places {@code filledItems} into {@code layout}'s non-empty slots, or row-major into a 3x3 grid. */
    private static CraftingInput grid(Entry layout, Item[] filledItems) {
        if (layout == null) {
            final List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
            for (int i = 0; i < filledItems.length; i++) stacks.set(i, stack(filledItems[i]));
            return CraftingInput.of(3, 3, stacks);
        }
        final List<ItemStack> stacks = new ArrayList<>();
        int next = 0;
        for (Set<Item> slot : layout.slots) stacks.add(slot == null ? ItemStack.EMPTY : stack(filledItems[next++]));
        return CraftingInput.of(layout.width, layout.height, stacks);
    }

    private static ItemStack stack(Item item) {
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static Item intersect(Set<Item> a, Set<Item> b) {
        for (Item i : a) if (b.contains(i)) return i;
        return null;
    }

    /** match[i] = index in {@code b} assigned to {@code a[i]}, sharing an item; null if impossible. */
    private static int[] perfectMatching(List<Set<Item>> a, List<Set<Item>> b) {
        final int[] owner = new int[b.size()];
        Arrays.fill(owner, -1);
        for (int i = 0; i < a.size(); i++) {
            if (!augment(i, a, b, owner, new boolean[b.size()])) return null;
        }
        final int[] match = new int[a.size()];
        for (int j = 0; j < owner.length; j++) match[owner[j]] = j;
        return match;
    }

    private static boolean augment(int i, List<Set<Item>> a, List<Set<Item>> b, int[] owner, boolean[] seen) {
        for (int j = 0; j < b.size(); j++) {
            if (seen[j] || intersect(a.get(i), b.get(j)) == null) continue;
            seen[j] = true;
            if (owner[j] < 0 || augment(owner[j], a, b, owner, seen)) {
                owner[j] = i;
                return true;
            }
        }
        return false;
    }
}
