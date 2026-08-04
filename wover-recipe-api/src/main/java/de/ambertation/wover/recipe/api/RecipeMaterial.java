package de.ambertation.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * A generic, deferrable description of a recipe ingredient: a tag, one or more items, one or more item stacks,
 * or a vanilla {@link Ingredient}, wrapped behind a single type so that builder methods
 * (e.g. {@link CraftingRecipeBuilder#addMaterial(char, RecipeMaterial)},
 * {@link CookingRecipeBuilder#input(RecipeMaterial)}, {@link StonecutterRecipeBuilder#input(RecipeMaterial)})
 * don't need an overload per ingredient kind.
 * <p>
 * Create instances with one of the {@code of(...)} factory methods, or with one of the {@code ofDeferred...}
 * variants when the underlying value (e.g. a tag or item supplied by another mod) isn't available yet at the
 * time the material is created and must be resolved lazily when the recipe is actually built.
 */
public interface RecipeMaterial {
    /**
     * Callback used by {@link #consume(Consumer)} to report which concrete kind of ingredient a
     * {@link RecipeMaterial} wraps. Exactly one of the methods is invoked.
     */
    interface Consumer {
        /**
         * Invoked when the material wraps a tag.
         *
         * @param tag The wrapped tag.
         */
        void apply(TagKey<Item> tag);

        /**
         * Invoked when the material wraps one or more item stacks.
         *
         * @param stacks The wrapped item stacks.
         */
        void apply(ItemStack... stacks);

        /**
         * Invoked when the material wraps one or more items.
         *
         * @param items The wrapped items.
         */
        void apply(ItemLike... items);

        /**
         * Invoked when the material wraps a vanilla ingredient.
         *
         * @param ingredient The wrapped ingredient.
         */
        void apply(Ingredient ingredient);
    }

    /**
     * Resolves this material (following any deferred supplier) and reports its concrete value to the
     * given consumer.
     *
     * @param consumer The consumer to report the resolved value to.
     */
    void consume(Consumer consumer);

    /**
     * Checks whether this material resolves to a usable, non-empty value (e.g. a tag reference is non-null, or
     * at least one wrapped item/stack is present).
     *
     * @return {@code true} if the material can be used to build a recipe.
     */
    boolean isValid();

    /**
     * Creates a material that wraps a tag.
     *
     * @param tag The tag to wrap.
     * @return The new material.
     */
    static RecipeMaterial of(TagKey<Item> tag) {
        return new MTag(tag);
    }

    /**
     * Creates a material that wraps one or more items.
     *
     * @param items The items to wrap.
     * @return The new material.
     */
    static RecipeMaterial of(ItemLike... items) {
        return new MItemLike(items);
    }

    /**
     * Creates a material that wraps one or more item stacks.
     *
     * @param stacks The item stacks to wrap.
     * @return The new material.
     */
    static RecipeMaterial of(ItemStack... stacks) {
        return new MItemStack(stacks);
    }

    /**
     * Creates a material that wraps a vanilla ingredient.
     *
     * @param ingredient The ingredient to wrap.
     * @return The new material.
     */
    static RecipeMaterial of(Ingredient ingredient) {
        return new MIngredient(ingredient);
    }

    /**
     * Creates a material whose concrete value is resolved lazily, at the time the recipe is built, by calling
     * the given supplier.
     *
     * @param mat The supplier that produces the actual material.
     * @return The new, deferred material.
     */
    static RecipeMaterial ofDeferred(Supplier<RecipeMaterial> mat) {
        return new MSupplier(mat);
    }

    /**
     * Creates a material that wraps a tag which is resolved lazily, at the time the recipe is built.
     *
     * @param tagSupplier The supplier that produces the tag.
     * @return The new, deferred material.
     */
    static RecipeMaterial ofDeferredTag(Supplier<TagKey<Item>> tagSupplier) {
        return new MSupplier(() -> new MTag(tagSupplier.get()));
    }

    /**
     * Creates a material that wraps an item stack which is resolved lazily, at the time the recipe is built.
     *
     * @param stacksSupplier The supplier that produces the item stack.
     * @return The new, deferred material.
     */
    static RecipeMaterial ofDeferredItemStack(Supplier<ItemStack> stacksSupplier) {
        return new MSupplier(() -> new MItemStack(stacksSupplier.get()));
    }

    /**
     * Creates a material that wraps an item which is resolved lazily, at the time the recipe is built.
     *
     * @param itemsSupplier The supplier that produces the item.
     * @return The new, deferred material.
     */
    static RecipeMaterial ofDeferredItemLike(Supplier<ItemLike> itemsSupplier) {
        return new MSupplier(() -> new MItemLike(itemsSupplier.get()));
    }

    /**
     * Creates a material that wraps an ingredient which is resolved lazily, at the time the recipe is built.
     *
     * @param ingredientSupplier The supplier that produces the ingredient.
     * @return The new, deferred material.
     */
    static RecipeMaterial ofDeferredIngredient(Supplier<Ingredient> ingredientSupplier) {
        return new MSupplier(() -> new MIngredient(ingredientSupplier.get()));
    }
}

class MTag implements RecipeMaterial {
    private final TagKey<Item> tag;

    public MTag(TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public void consume(Consumer consumer) {
        consumer.apply(tag);
    }

    @Override
    public boolean isValid() {
        return tag != null;
    }
}

class MItemStack implements RecipeMaterial {
    private final ItemStack[] itemStacks;

    public MItemStack(ItemStack... itemStacks) {
        this.itemStacks = Arrays
                .stream(itemStacks)
                .filter(s -> s != null && !s.isEmpty())
                .toArray(ItemStack[]::new);
    }

    @Override
    public void consume(Consumer consumer) {
        consumer.apply(this.itemStacks);
    }

    @Override
    public boolean isValid() {
        for (ItemStack stack : itemStacks) {
            if (stack != null && !stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

class MItemLike implements RecipeMaterial {
    private final ItemLike[] items;

    public MItemLike(ItemLike... items) {
        this.items = Arrays.stream(items)
                           .filter(item -> item != null)
                           .toArray(ItemLike[]::new);
    }

    @Override
    public void consume(Consumer consumer) {
        consumer.apply(items);
    }

    @Override
    public boolean isValid() {
        for (ItemLike item : items) {
            if (item != null) return true;
        }
        return false;
    }
}


class MIngredient implements RecipeMaterial {
    private final Ingredient ingredient;

    public MIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public void consume(Consumer consumer) {
        consumer.apply(ingredient);
    }

    @Override
    public boolean isValid() {
        return ingredient != null && !ingredient.isEmpty();
    }
}

class MSupplier implements RecipeMaterial {
    private final Supplier<RecipeMaterial> mat;

    public MSupplier(Supplier<RecipeMaterial> mat) {
        this.mat = mat;
    }

    @Override
    public void consume(Consumer consumer) {
        this.mat.get().consume(consumer);
    }

    @Override
    public boolean isValid() {
        return this.mat.get().isValid();
    }
}



