package org.betterx.wover.recipe.api;

import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.data.recipes.RecipeCategory;

public class RecipeTraitLibrary {
    public static BlockRecipeTrait planks(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    if (!sourceMaterial.isValid()) {
                        LibWoverSets.C.LOG.warn(
                                "Skipping planks recipe for {}: source material is invalid",
                                key.location()
                        );
                        return;
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(4)
                            .shapeless()
                            .addMaterial('#', sourceMaterial)
                            .group("planks")
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait slab(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    if (!sourceMaterial.isValid()) {
                        LibWoverSets.C.LOG.warn(
                                "Skipping slab recipe for {}: source material is invalid",
                                key.location()
                        );
                        return;
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(6)
                            .shape("###")
                            .addMaterial('#', sourceMaterial)
                            .group("slab")
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait log(RecipeMaterial barkMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    if (!barkMaterial.isValid()) {
                        LibWoverSets.C.LOG.warn(
                                "Skipping log recipe for {}: bark material is invalid",
                                key.location()
                        );
                        return;
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##", "##")
                            .addMaterial('#', barkMaterial)
                            .outputCount(3)
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }


    public static BlockRecipeTrait bark(RecipeMaterial logMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    if (!logMaterial.isValid()) {
                        LibWoverSets.C.LOG.warn(
                                "Skipping bark recipe for {}: log material is invalid",
                                key.location()
                        );
                        return;
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##", "##")
                            .addMaterial('#', logMaterial)
                            .outputCount(3)
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }
}
