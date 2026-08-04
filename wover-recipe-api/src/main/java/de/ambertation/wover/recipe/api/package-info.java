/**
 * Fluent, code-first builders for Minecraft recipes.
 * <p>
 * {@link de.ambertation.wover.recipe.api.RecipeBuilder} is the entry point: its static factory methods
 * ({@link de.ambertation.wover.recipe.api.RecipeBuilder#crafting}, {@link de.ambertation.wover.recipe.api.RecipeBuilder#cooking}
 * and friends, {@link de.ambertation.wover.recipe.api.RecipeBuilder#smithing},
 * {@link de.ambertation.wover.recipe.api.RecipeBuilder#stonecutting}) return a
 * {@link de.ambertation.wover.recipe.api.BaseRecipeBuilder} subtype for the desired recipe type, which is
 * configured with a chain of fluent calls and finished with
 * {@link de.ambertation.wover.recipe.api.BaseRecipeBuilder#build(de.ambertation.wover.recipe.api.RecipeBuilder.Context)}.
 * <p>
 * The same builders can be used in two places: from a
 * {@link de.ambertation.wover.datagen.api.provider.WoverRecipeProvider} during datagen (to write the recipe as a
 * JSON file), or from an {@link de.ambertation.wover.recipe.api.OnBootstrapRecipes} subscriber registered on
 * {@link de.ambertation.wover.recipe.api.RecipeBuilder#BOOTSTRAP_RECIPES} (to add the recipe to a running game
 * without a JSON file backing it). Both paths are handed a matching
 * {@link de.ambertation.wover.recipe.api.RecipeBuilder.Context}.
 * <p>
 * {@link de.ambertation.wover.recipe.api.RecipeMaterial} is a small abstraction over "an ingredient" (tag, item(s),
 * item stack(s), or a vanilla {@link net.minecraft.world.item.crafting.Ingredient}) accepted by several builder
 * methods so mod code doesn't need to pick an overload up front.
 */
package de.ambertation.wover.recipe.api;
