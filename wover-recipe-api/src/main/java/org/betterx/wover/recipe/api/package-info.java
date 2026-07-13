/**
 * Fluent, code-first builders for Minecraft recipes.
 * <p>
 * {@link org.betterx.wover.recipe.api.RecipeBuilder} is the entry point: its static factory methods
 * ({@link org.betterx.wover.recipe.api.RecipeBuilder#crafting}, {@link org.betterx.wover.recipe.api.RecipeBuilder#cooking}
 * and friends, {@link org.betterx.wover.recipe.api.RecipeBuilder#smithing},
 * {@link org.betterx.wover.recipe.api.RecipeBuilder#stonecutting}) return a
 * {@link org.betterx.wover.recipe.api.BaseRecipeBuilder} subtype for the desired recipe type, which is
 * configured with a chain of fluent calls and finished with
 * {@link org.betterx.wover.recipe.api.BaseRecipeBuilder#build(org.betterx.wover.recipe.api.RecipeBuilder.Context)}.
 * <p>
 * The same builders can be used in two places: from a
 * {@link org.betterx.wover.datagen.api.provider.WoverRecipeProvider} during datagen (to write the recipe as a
 * JSON file), or from an {@link org.betterx.wover.recipe.api.OnBootstrapRecipes} subscriber registered on
 * {@link org.betterx.wover.recipe.api.RecipeBuilder#BOOTSTRAP_RECIPES} (to add the recipe to a running game
 * without a JSON file backing it). Both paths are handed a matching
 * {@link org.betterx.wover.recipe.api.RecipeBuilder.Context}.
 * <p>
 * {@link org.betterx.wover.recipe.api.RecipeMaterial} is a small abstraction over "an ingredient" (tag, item(s),
 * item stack(s), or a vanilla {@link net.minecraft.world.item.crafting.Ingredient}) accepted by several builder
 * methods so mod code doesn't need to pick an overload up front.
 */
package org.betterx.wover.recipe.api;
