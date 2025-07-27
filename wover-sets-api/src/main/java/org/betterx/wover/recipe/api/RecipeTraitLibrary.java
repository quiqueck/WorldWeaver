package org.betterx.wover.recipe.api;

import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.item.impl.trait.ItemRecipeTraitBuilder;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.Nullable;

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
        return slab(planksMaterial, "slab", true);
    }

    public static BlockRecipeTrait slab(RecipeMaterial planksMaterial, String group, boolean withStonecutter) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "slab", "planks");

                    if (withStonecutter) {
                        RecipeBuilder
                                .stonecutting(
                                        key.location().withPrefix("stonecutter_"),
                                        block
                                )
                                .input(planksMaterial)
                                .outputCount(2)
                                .group(group)
                                .build(context);
                    }

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


    public static BlockRecipeTrait pillar(RecipeMaterial slabMaterial, @Nullable RecipeMaterial stoneMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(slabMaterial, "pillar", "slab");

                    if (stoneMaterial != null) {
                        validOrThrow(stoneMaterial, "pillar", "stone");

                        RecipeBuilder
                                .stonecutting(key.location().withPrefix("_stonecutting"), block)
                                .input(stoneMaterial)
                                .build(context);
                    }

                    RecipeBuilder.crafting(key.location(), block)
                                 .shape("#", "#")
                                 .addMaterial('#', slabMaterial)
                                 .group("end_pillar")
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

    public static BlockRecipeTrait gate(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "gate", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("I#I", "I#I")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('I', Items.STICK)
                            .group("gate")
                            .category(RecipeCategory.REDSTONE)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait hangingSign(RecipeMaterial strippedLogMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(strippedLogMaterial, "hangingSign", "stripped_log");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3)
                            .shape("I I", "###", "###")
                            .addMaterial('#', strippedLogMaterial)
                            .addMaterial('I', Items.CHAIN)
                            .group("hanging_sign")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait sign(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "sign", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3)
                            .shape("###", "###", " I ")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('I', Items.STICK)
                            .group("sign")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait ladder(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "sign", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(3).shape("I I", "I#I", "I I")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('I', Items.STICK)
                            .group("ladder")
                            .category(RecipeCategory.DECORATIONS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait pressurePlate(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "plate", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("##")
                            .addMaterial('#', planksMaterial)
                            .group("pressure_plate")
                            .category(RecipeCategory.REDSTONE)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait stairs(RecipeMaterial planksMaterial) {
        return stairs(planksMaterial, "stairs", true);
    }

    public static BlockRecipeTrait stairs(RecipeMaterial planksMaterial, String group, boolean withStonecutter) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "stairs", "planks");

                    if (withStonecutter) {
                        RecipeBuilder
                                .stonecutting(
                                        key.location().withPrefix("stonecutter_"),
                                        block
                                )
                                .input(planksMaterial)
                                .outputCount(1)
                                .group(group)
                                .build(context);
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(4)
                            .shape("#  ", "## ", "###")
                            .addMaterial('#', planksMaterial)
                            .group(group)
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait trapdoor(RecipeMaterial planksMaterial) {
        return trapdoor(planksMaterial, "trapdoor");
    }

    public static BlockRecipeTrait trapdoor(RecipeMaterial planksMaterial, String group) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "trapdoor", "planks");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(2).shape("###", "###")
                            .addMaterial('#', planksMaterial)
                            .group(group)
                            .category(RecipeCategory.REDSTONE)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait wall(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "wall", "source");

                    RecipeBuilder
                            .stonecutting(
                                    key.location().withPrefix("stonecutter_"),
                                    block
                            )
                            .input(sourceMaterial)
                            .outputCount(1)
                            .group("wall")
                            .build(context);

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(6)
                            .shape("***", "***")
                            .addMaterial('*', sourceMaterial)
                            .group("wall")
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait woodWall(RecipeMaterial planksMaterial, RecipeMaterial fenceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "wall", "planks");
                    validOrThrow(fenceMaterial, "wall", "fence");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(6)
                            .shape("* *", "|||")
                            .addMaterial('*', planksMaterial)
                            .addMaterial('|', fenceMaterial)
                            .group("wooden_wall")
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait brickSource(RecipeMaterial sourceMaterial, boolean withStonecutter) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "brickSource", "source");


                    if (withStonecutter) {
                        RecipeBuilder
                                .stonecutting(
                                        key.location().withPrefix("stonecutter_"),
                                        block
                                )
                                .input(sourceMaterial)
                                .outputCount(1)
                                .group("brick")
                                .build(context);
                    }

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(4)
                            .shape("**", "**")
                            .addMaterial('*', sourceMaterial)
                            .group("brick")
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait crackedSource(RecipeMaterial sourceMaterial, boolean withStonecutter) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "crackedSource", "source");


                    if (withStonecutter) {
                        RecipeBuilder
                                .stonecutting(
                                        key.location().withPrefix("stonecutter_"),
                                        block
                                )
                                .input(sourceMaterial)
                                .outputCount(1)
                                .group("cracked")
                                .build(context);
                    }

                    RecipeBuilder
                            .blasting(key.location(), block)
                            .input(sourceMaterial)
                            .experience(0.1f)
                            .cookingTime(200)
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait stoneCutSource(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "stoneCutSource", "source");

                    RecipeBuilder
                            .stonecutting(
                                    key.location().withPrefix("stonecutter_"),
                                    block
                            )
                            .input(sourceMaterial)
                            .outputCount(1)
                            .group("cracked")
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait mossySource(RecipeMaterial sourceMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(sourceMaterial, "chiseledSource", "source");

                    RecipeBuilder
                            .crafting(
                                    key.location().withSuffix("_from_moss_block"),
                                    block
                            )
                            .shapeless()
                            .addMaterial('M', Blocks.MOSS_BLOCK)
                            .addMaterial('#', sourceMaterial)
                            .outputCount(1)
                            .group("mossy")
                            .build(context);

                    RecipeBuilder
                            .crafting(
                                    key.location().withSuffix("_from_vine"),
                                    block
                            )
                            .shapeless()
                            .addMaterial('M', CommonItemTags.VINES)
                            .addMaterial('#', sourceMaterial)
                            .outputCount(1)
                            .group("mossy")
                            .build(context);
                }
        );
    }
}
