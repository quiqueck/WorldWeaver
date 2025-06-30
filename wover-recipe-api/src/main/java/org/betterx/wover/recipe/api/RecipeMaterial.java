package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.function.Supplier;

public interface RecipeMaterial {
    interface Consumer {
        void apply(TagKey<Item> tag);
        void apply(ItemStack... stacks);
        void apply(ItemLike... items);
        void apply(Ingredient ingredient);
    }

    void consume(Consumer consumer);
    boolean isValid();

    static RecipeMaterial of(TagKey<Item> tag) {
        return new MTag(tag);
    }
    static RecipeMaterial of(ItemLike... items) {
        return new MItemLike(items);
    }
    static RecipeMaterial of(ItemStack... stacks) {
        return new MItemStack(stacks);
    }
    static RecipeMaterial of(Ingredient ingredient) {
        return new MIngredient(ingredient);
    }
    static RecipeMaterial ofDeferred(Supplier<RecipeMaterial> mat) {
        return new MSupplier(mat);
    }
    static RecipeMaterial ofDeferredTag(Supplier<TagKey<Item>> tagSupplier) {
        return new MSupplier(() -> new MTag(tagSupplier.get()));
    }
    static RecipeMaterial ofDeferredItemStack(Supplier<ItemStack> stacksSupplier) {
        return new MSupplier(() -> new MItemStack(stacksSupplier.get()));
    }
    static RecipeMaterial ofDeferredItemLike(Supplier<ItemLike> itemsSupplier) {
        return new MSupplier(() -> new MItemLike(itemsSupplier.get()));
    }
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



