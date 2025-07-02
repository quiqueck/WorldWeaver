package org.betterx.wover.recipe.api;

import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.item.impl.trait.ItemRecipeTraitBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

public class RecipeTraitLibrary {
    protected static void validOrThrow(RecipeMaterial sourceMaterial, String recipeType, String materialType) {
        if (!sourceMaterial.isValid()) {
            LibWoverSets.C.LOG.warn(
                    "Skipping {} recipe: {} material is invalid",
                    recipeType, materialType
            );
        }
    }

    public static BlockRecipeTrait planks(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "planks", "source");

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

    public static BlockRecipeTrait slab(RecipeMaterial planksMaterial) {
        return slab(planksMaterial, "slab");
    }

    public static BlockRecipeTrait slab(RecipeMaterial planksMaterial, String group) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "slab", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(6)
                            .shape("###")
                            .addMaterial('#', planksMaterial)
                            .group(group)
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait log(RecipeMaterial barkMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(barkMaterial, "log", "bark");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##", "##")
                            .addMaterial('#', barkMaterial)
                            .outputCount(3)
                            .group("log")
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait bark(RecipeMaterial logMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(logMaterial, "bark", "log");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##", "##")
                            .addMaterial('#', logMaterial)
                            .outputCount(3)
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .group("bark")
                            .build(context);
                }
        );
    }


    public static BlockRecipeTrait barrel(RecipeMaterial planksMaterial, RecipeMaterial slabMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "barrel", "planks");
                    validOrThrow(slabMaterial, "barrel", "slab");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("#S#", "# #", "#S#")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('S', slabMaterial)
                            .group("barrel")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static ItemRecipeTrait boat(RecipeMaterial planksMaterial) {
        return ItemRecipeTraitBuilder.BUILDER.with(
                (key, item, context) -> {
                    validOrThrow(planksMaterial, "boat", "planks");

                    RecipeBuilder
                            .crafting(key.location(), item)
                            .shape("# #", "###")
                            .addMaterial('#', planksMaterial)
                            .group("boat")
                            .category(RecipeCategory.TRANSPORTATION)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait bookshelf(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "bookshelf", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("###", "PPP", "###")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('P', Items.BOOK)
                            .group("bookshelf")
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait button(RecipeMaterial planksMaterial) {
        return button(planksMaterial, "button");
    }

    public static BlockRecipeTrait button(RecipeMaterial planksMaterial, String group) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "button", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shapeless()
                            .addMaterial('#', planksMaterial)
                            .group(group)
                            .category(RecipeCategory.REDSTONE)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait chest(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "chest", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("###", "# #", "###")
                            .addMaterial('#', planksMaterial)
                            .group("chest")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static ItemRecipeTrait chestBoat(RecipeMaterial boatMaterial, RecipeMaterial chestMaterial) {
        return ItemRecipeTraitBuilder.BUILDER.with(
                (key, item, context) -> {
                    validOrThrow(boatMaterial, "chestBoat", "boat");
                    validOrThrow(chestMaterial, "chestBoat", "chest");

                    RecipeBuilder
                            .crafting(key.location(), item)
                            .shapeless()
                            .addMaterial('C', chestMaterial)
                            .addMaterial('#', boatMaterial)
                            .group("chest_boat")
                            .category(RecipeCategory.TRANSPORTATION)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait composter(RecipeMaterial slabMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(slabMaterial, "composter", "slab");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("# #", "# #", "###")
                            .addMaterial('#', slabMaterial)
                            .group("composter")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait craftingTable(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "craftingTable", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##", "##")
                            .addMaterial('#', planksMaterial)
                            .group("crafting_table")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait door(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "door", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3)
                            .shape("##", "##", "##")
                            .addMaterial('#', planksMaterial)
                            .group("door")
                            .category(RecipeCategory.REDSTONE)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait fence(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "fence", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3)
                            .shape("#I#", "#I#")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('I', Items.STICK)
                            .group("fence")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }
}
