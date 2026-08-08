package de.ambertation.wover.recipe.api;

import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.impl.trait.BlockRecipeTraitBuilder;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.item.impl.trait.ItemRecipeTraitBuilder;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.Nullable;

/**
 * Ready-made {@link BlockRecipeTrait}/{@link ItemRecipeTrait} factories for every block/item type in
 * {@code de.ambertation.wover.sets.api.blocks.types}, matching vanilla's own crafting/stonecutting recipes for the
 * corresponding wood/stone family member (plank-to-slab, planks-to-stairs, log-to-bark, ...).
 * <p>
 * Every method returns a trait built with {@code BlockRecipeTraitBuilder}/{@code ItemRecipeTraitBuilder}, so it
 * can be passed directly to {@code BlockDefinition#addTrait}/{@code ItemDefinition#addTrait} — the recipe is then
 * generated automatically during recipe datagen (see the {@code recipe-api} wiki page's "Recipe traits" section).
 * A {@link RecipeMaterial} argument that turns out {@link RecipeMaterial#isValid() invalid} once the recipe is
 * actually built (e.g. a deferred slot that was never registered) causes that recipe to be skipped with a
 * warning, rather than failing datagen.
 */
public class RecipeTraitLibrary {
    /**
     * Logs a warning and lets the caller skip building a recipe if {@code sourceMaterial} turns out invalid.
     *
     * @param sourceMaterial the material to validate
     * @param recipeType     the kind of recipe being built, for the warning message
     * @param materialType   the role of {@code sourceMaterial} in the recipe, for the warning message
     */
    protected static void validOrThrow(RecipeMaterial sourceMaterial, String recipeType, String materialType) {
        if (!sourceMaterial.isValid()) {
            LibWoverSets.C.LOG.warn(
                    "Skipping {} recipe: {} material is invalid",
                    recipeType, materialType
            );
        }
    }

    /**
     * A vanilla-style planks recipe: 4 planks, shapeless, from one source (log/bark/stem) material.
     *
     * @param sourceMaterial the log/bark/stem material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style slab recipe (with a matching stonecutting recipe) in the {@code "slab"} group.
     *
     * @param planksMaterial the base material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait slab(RecipeMaterial planksMaterial) {
        return slab(planksMaterial, "slab", true);
    }

    /**
     * A vanilla-style slab recipe: 6 slabs, 3-across shaped, plus an optional stonecutting recipe.
     *
     * @param planksMaterial  the base material this recipe consumes
     * @param group           the recipe group to use
     * @param withStonecutter whether a matching stonecutting recipe should also be generated
     * @return the recipe trait
     */
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

    /**
     * A recipe turning 4 bark blocks into 3 logs (2x2 shaped).
     *
     * @param barkMaterial the bark material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A recipe turning 4 log blocks into 3 bark blocks (2x2 shaped) - the inverse of {@link #log}.
     *
     * @param logMaterial the log material this recipe consumes
     * @return the recipe trait
     */
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


    /**
     * A vanilla-style barrel recipe: planks around the sides, slabs top and bottom center.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @param slabMaterial   the slab material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style boat recipe: 5 planks, shaped.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style bookshelf recipe: planks around 3 books in the middle row.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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


    /**
     * A vanilla-style chiseled bookshelf recipe: two rows of planks around a row of slabs.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @param slabMaterial   the slab material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait chiseledBookshelf(
            RecipeMaterial planksMaterial,
            RecipeMaterial slabMaterial
    ) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "chiseled bookshelf", "planks");
                    validOrThrow(slabMaterial, "chiseled bookshelf", "slab");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("###", "SSS", "###")
                            .addMaterial('#', planksMaterial)
                            .addMaterial('S', slabMaterial)
                            .group("chiseled_bookshelf")
                            .category(RecipeCategory.BUILDING_BLOCKS)
                            .build(context);
                }
        );
    }

    /**
     * A recipe stacking 2 slabs into a rotated pillar block, plus an optional stonecutting recipe from a source
     * stone material.
     *
     * @param slabMaterial  the slab material this recipe consumes
     * @param stoneMaterial the source stone material for a stonecutting recipe, or {@code null} to skip it
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style button recipe (shapeless, single source item) in the {@code "button"} group.
     *
     * @param planksMaterial the base material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait button(RecipeMaterial planksMaterial) {
        return button(planksMaterial, "button");
    }

    /**
     * A vanilla-style button recipe: shapeless, single source item.
     *
     * @param planksMaterial the base material this recipe consumes
     * @param group          the recipe group to use (e.g. {@code "wooden_button"}/{@code "stone_button"})
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style chest recipe: a ring of 8 planks.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style chest boat recipe: shapeless, one boat plus one chest.
     *
     * @param boatMaterial  the boat material this recipe consumes
     * @param chestMaterial the chest material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style composter recipe: 7 slabs, sides and bottom open at the top.
     *
     * @param slabMaterial the slab material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style crafting table recipe: 4 planks, 2x2 shaped.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style door recipe: 6 planks, 2-wide/3-tall shaped, yields 3 doors.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style fence recipe: planks and sticks, yields 3 fences.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style fence gate recipe: planks and sticks.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style hanging sign recipe: stripped logs plus 2 chains, yields 3 hanging signs.
     *
     * @param strippedLogMaterial the stripped log material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style sign recipe: 6 planks plus a stick, yields 3 signs.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style ladder recipe: planks and sticks, yields 3 ladders.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait ladder(RecipeMaterial planksMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(planksMaterial, "ladder", "planks");

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

    /**
     * A vanilla-style pressure plate recipe: 2 planks side by side, in the {@code "pressure_plate"} group.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style stairs recipe (with a matching stonecutting recipe) in the {@code "stairs"} group.
     *
     * @param planksMaterial the base material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait stairs(RecipeMaterial planksMaterial) {
        return stairs(planksMaterial, "stairs", true);
    }

    /**
     * A vanilla-style stairs recipe: staircase-shaped, yields 4 stairs, plus an optional stonecutting recipe.
     *
     * @param planksMaterial  the base material this recipe consumes
     * @param group           the recipe group to use
     * @param withStonecutter whether a matching stonecutting recipe should also be generated
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style trapdoor recipe in the {@code "trapdoor"} group.
     *
     * @param planksMaterial the base material this recipe consumes
     * @return the recipe trait
     */
    public static BlockRecipeTrait trapdoor(RecipeMaterial planksMaterial) {
        return trapdoor(planksMaterial, "trapdoor");
    }

    /**
     * A vanilla-style trapdoor recipe: 6 planks, 2 rows of 3, yields 2 trapdoors.
     *
     * @param planksMaterial the base material this recipe consumes
     * @param group          the recipe group to use (e.g. {@code "wooden_trapdoor"})
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style stone wall recipe: 6 source blocks, 2 rows of 3, plus a matching stonecutting recipe.
     *
     * @param sourceMaterial the base material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A WoVer-specific "wooden wall" recipe (vanilla has no wooden walls): planks over a row of fences, yields 6
     * walls, in the {@code "wooden_wall"} group. No stonecutting recipe is generated.
     *
     * @param planksMaterial the planks material this recipe consumes
     * @param fenceMaterial  the fence material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style "bricks" recipe: 4 source blocks, 2x2 shaped, plus an optional stonecutting recipe.
     *
     * @param sourceMaterial  the base material this recipe consumes
     * @param withStonecutter whether a matching stonecutting recipe should also be generated
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style "cracked" recipe: blasting the source material, plus an optional stonecutting recipe.
     *
     * @param sourceMaterial  the base material this recipe consumes
     * @param withStonecutter whether a matching stonecutting recipe should also be generated
     * @return the recipe trait
     */
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

    /**
     * A single stonecutting recipe from the source material (used for chiseled/polished/tiled variants that have
     * no crafting recipe, only a stonecutting one).
     *
     * @param sourceMaterial the base material this recipe consumes
     * @return the recipe trait
     */
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

    /**
     * A vanilla-style "mossy" recipe: two shapeless variants, one from a moss block and one from vines
     * ({@link de.ambertation.wover.tag.api.predefined.CommonItemTags#VINES}), each combined with the source
     * material.
     *
     * @param sourceMaterial the base material this recipe consumes
     * @return the recipe trait
     */
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

    public static BlockRecipeTrait chain(RecipeMaterial ingotMaterial, RecipeMaterial nuggetMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(ingotMaterial, "chain", "ingot");
                    validOrThrow(nuggetMaterial, "chain", "nugget");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .shape("N", "#", "N")
                            .addMaterial('#', ingotMaterial)
                            .addMaterial('N', nuggetMaterial)
                            .group("metal_chain")
                            .build(context);
                }
        );
    }

    public static BlockRecipeTrait bars(RecipeMaterial ingotMaterial) {
        return BlockRecipeTraitBuilder.BUILDER.with(
                (key, block, context) -> {
                    validOrThrow(ingotMaterial, "chain", "ingot");

                    RecipeBuilder
                            .crafting(key.location(), block)
                            .outputCount(16)
                            .shape("###", "###")
                            .addMaterial('#', ingotMaterial)
                            .group("metal_bars")
                            .build(context);
                }
        );
    }
}
