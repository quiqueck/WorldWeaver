package org.betterx.wover.recipe.api;

import org.betterx.wover.recipe.impl.SmithingRecipeBuilderImpl;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Fluent builder for smithing-table transform recipes (upgrading a base item with a smithing template and an
 * addon material, e.g. netherite upgrades).
 * <p>
 * A complete recipe needs a {@link #template(SmithingTemplateItem)}, a {@link #base}, and an
 * {@link #addon} to be set before {@link #build(RecipeBuilder.Context)} is called; the output must have a
 * count of exactly {@code 1}.
 * <p>
 * Obtain an instance through {@link RecipeBuilder#smithing}.
 */
public interface SmithingRecipeBuilder extends BaseRecipeBuilder<SmithingRecipeBuilder>, BaseUnlockableRecipeBuilder<SmithingRecipeBuilder> {
    /**
     * Sets the smithing template item required in the template slot. Also adds an unlock criterion for it.
     *
     * @param in The smithing template item.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl template(SmithingTemplateItem in);

    /**
     * Sets the base slot's accepted ingredient to any item from the given tag.
     *
     * @param in The base tag.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl base(TagKey<Item> in);

    /**
     * Sets the base slot's accepted ingredient to the given item.
     *
     * @param in The base item.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl base(ItemLike in);

    /**
     * Sets the base slot's accepted ingredient directly.
     *
     * @param in The base ingredient.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl base(Ingredient in);

    /**
     * Sets the addon slot's accepted ingredient to any item from the given tag.
     *
     * @param in The addon tag.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl addon(TagKey<Item> in);

    /**
     * Sets the addon slot's accepted ingredient to the given item.
     *
     * @param in The addon item.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl addon(ItemLike in);

    /**
     * Sets the addon slot's accepted ingredient directly.
     *
     * @param in The addon ingredient.
     * @return This builder, for chaining.
     */
    SmithingRecipeBuilderImpl addon(Ingredient in);
}
