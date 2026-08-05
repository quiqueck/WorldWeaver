package de.ambertation.wover.recipe.api;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.recipe.impl.*;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * Entry point for building recipes in code and writing them either to a live {@link net.minecraft.world.item.crafting.RecipeManager}
 * (at runtime, via {@link #BOOTSTRAP_RECIPES}) or to recipe JSON files (during datagen, via
 * {@link de.ambertation.wover.datagen.api.provider.WoverRecipeProvider}).
 * <p>
 * Every static factory method here (e.g. {@link #crafting}, {@link #cooking}, {@link #smithing},
 * {@link #stonecutting}) returns a fluent builder for one recipe type; configure it and finish with
 * {@link BaseRecipeBuilder#build(Context)}.
 */
public class RecipeBuilder {
    /**
     * The context a recipe is built against: bundles the registry lookups and the {@link RecipeOutput} the
     * finished recipe is written to.
     * <p>
     * An instance is handed to every {@link OnBootstrapRecipes} subscriber and to every
     * {@link de.ambertation.wover.datagen.api.provider.WoverRecipeProvider}; it should not usually be constructed
     * directly by mod code.
     *
     * @param lookupProvider The registry lookup used to resolve dynamic registries (e.g. items).
     * @param itemLookup     The {@link HolderGetter} used to resolve {@link Item} holders, derived from
     *                       {@code lookupProvider} unless supplied explicitly.
     * @param recipeProvider The vanilla {@link RecipeProvider} used to derive advancement criteria and tag
     *                       ingredients.
     * @param recipeOutput   The output every built recipe is written to.
     */
    public record Context(HolderLookup.Provider lookupProvider, HolderGetter<Item> itemLookup,
                          RecipeProvider recipeProvider, RecipeOutput recipeOutput) {
        /**
         * Creates a context that derives its item lookup from the given lookup provider.
         *
         * @param lookupProvider The registry lookup used to resolve dynamic registries.
         * @param recipeProvider The vanilla {@link RecipeProvider} used to derive advancement criteria and tag
         *                       ingredients.
         * @param recipeOutput   The output every built recipe is written to.
         */
        public Context(HolderLookup.Provider lookupProvider, RecipeProvider recipeProvider, RecipeOutput recipeOutput) {
            this(lookupProvider, lookupProvider.lookupOrThrow(Registries.ITEM), recipeProvider, recipeOutput);
        }

        /**
         * Creates a context with a no-op {@link RecipeProvider}, useful when recipes are built outside of the
         * datagen {@link RecipeProvider} lifecycle (e.g. from the {@link #BOOTSTRAP_RECIPES} runtime event).
         *
         * @param lookupProvider The registry lookup used to resolve dynamic registries.
         * @param recipeOutput   The output every built recipe is written to.
         */
        public Context(HolderLookup.Provider lookupProvider, RecipeOutput recipeOutput) {
            this(
                    lookupProvider,
                    lookupProvider.lookupOrThrow(Registries.ITEM),
                    new RecipeProvider(lookupProvider, recipeOutput) {
                        @Override
                        public void buildRecipes() {

                        }
                    },
                    recipeOutput
            );
        }

        /**
         * Creates an unlock-advancement criterion that is satisfied once the player has the given item.
         *
         * @param value The item.
         * @return The criterion.
         */
        public Criterion<?> has(Item value) {
            return this.recipeProvider.has(value);
        }

        /**
         * Creates an unlock-advancement criterion that is satisfied once the player has an item from the
         * given tag.
         *
         * @param value The tag.
         * @return The criterion.
         */
        public Criterion<?> has(TagKey<Item> value) {
            return this.recipeProvider.has(value);
        }

        /**
         * Creates an {@link Ingredient} that accepts any item from the given tag.
         *
         * @param in The tag.
         * @return The ingredient.
         */
        public Ingredient tag(TagKey<Item> in) {
            return this.recipeProvider.tag(in);
        }
    }

    /**
     * Runtime event fired while the vanilla {@link net.minecraft.world.item.crafting.RecipeManager} is applying
     * loaded recipes. Subscribers can add additional recipes on top of the ones loaded from datapacks, using the
     * same builders as datagen.
     */
    public static Event<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            RecipeRuntimeProviderImpl.BOOTSTRAP_RECIPES;

    /**
     * Starts building a crafting-table recipe (shaped by default).
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CraftingRecipeBuilder crafting(Identifier id, ItemLike output) {
        return new CraftingRecipeBuilderImpl(id, output);
    }

    /**
     * Starts building a stonecutter recipe.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static StonecutterRecipeBuilder stonecutting(Identifier id, ItemLike output) {
        return new StonecutterRecipeBuilderImpl(id, output);
    }

    /**
     * Starts building a smithing-table transform recipe.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static SmithingRecipeBuilder smithing(Identifier id, ItemLike output) {
        return new SmithingRecipeBuilderImpl(id, output);
    }

    /**
     * Starts building a cooking recipe with no device enabled yet. Use the {@code enable...} methods on the
     * returned builder (e.g. {@link CookingRecipeBuilder#enableSmelter()}) to pick which devices it applies to.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder cooking(Identifier id, ItemLike output) {
        return new CookingRecipeBuilderImpl(id, output, false, false, false, false);
    }

    /**
     * Starts building a cooking recipe for both the campfire and the smoker, matching how vanilla food items
     * are usually cookable.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder cookableFood(
            Identifier id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, true, true, false);
    }

    /**
     * Starts building a cooking recipe for the furnace only.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder smelting(Identifier id, ItemLike output) {
        return new CookingRecipeBuilderImpl(id, output, false, false, false, true);
    }

    /**
     * Starts building a cooking recipe for both the blast furnace and the furnace.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder blasting(
            Identifier id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, true, false, false, true);
    }

    /**
     * Starts building a cooking recipe for the smoker only.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder smoker(
            Identifier id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, false, true, false);
    }

    /**
     * Starts building a cooking recipe for the campfire only.
     *
     * @param id     The recipe id.
     * @param output The recipe's output item.
     * @return The new builder.
     */
    public static CookingRecipeBuilder campfire(
            Identifier id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, true, false, false);
    }

    private static CraftingRecipeBuilder copySmithingTemplateBase(
            Identifier id,
            ItemLike filler,
            ItemLike output
    ) {
        return crafting(id, output)
                .outputCount(2)
                .category(RecipeCategory.MISC)
                .addMaterial('#', filler)
                .addMaterial('S', output)
                .shape("#S#", "#C#", "###");
    }

    /**
     * Starts building a crafting recipe that duplicates a smithing template, mirroring vanilla's own
     * "copy smithing template" recipes: the pattern is filler/template/filler over filler/center/filler over
     * filler/filler/filler, and produces {@code 2} copies of the template.
     *
     * @param id              The recipe id.
     * @param costLevel       Determines which filler item surrounds the template (see
     *                        {@link CopySmithingTemplateCostLevel}).
     * @param outputTemplate  The smithing template item to duplicate; also used as the recipe's input template.
     * @param centerIngredient The item placed in the recipe's center slot.
     * @return The new builder, ready to be built.
     */
    public static CraftingRecipeBuilder copySmithingTemplate(
            Identifier id,
            CopySmithingTemplateCostLevel costLevel,
            ItemLike outputTemplate,
            ItemLike centerIngredient
    ) {
        return copySmithingTemplateBase(id, costLevel.priceItem, outputTemplate).addMaterial('C', centerIngredient);
    }

    /**
     * Starts building a crafting recipe that duplicates a smithing template, mirroring vanilla's own
     * "copy smithing template" recipes: the pattern is filler/template/filler over filler/center/filler over
     * filler/filler/filler, and produces {@code 2} copies of the template.
     *
     * @param id              The recipe id.
     * @param costLevel       Determines which filler item surrounds the template (see
     *                        {@link CopySmithingTemplateCostLevel}).
     * @param outputTemplate  The smithing template item to duplicate; also used as the recipe's input template.
     * @param centerIngredient The tag accepted in the recipe's center slot.
     * @return The new builder, ready to be built.
     */
    public static CraftingRecipeBuilder copySmithingTemplate(
            Identifier id,
            CopySmithingTemplateCostLevel costLevel,
            ItemLike outputTemplate,
            TagKey<Item> centerIngredient
    ) {
        return copySmithingTemplateBase(id, costLevel.priceItem, outputTemplate).addMaterial('C', centerIngredient);
    }

    /**
     * The filler item surrounding a smithing template in a {@link #copySmithingTemplate} recipe, mirroring the
     * three cost tiers vanilla uses for its own template-duplication recipes.
     */
    public enum CopySmithingTemplateCostLevel {
        /** Filled with {@link Items#STICK}. */
        CHEAP(Items.STICK),
        /** Filled with {@link Items#DIAMOND}. */
        REGULAR(Items.DIAMOND),
        /** Filled with {@link Items#NETHERITE_SCRAP}. */
        EXPENSIVE(Items.NETHERITE_SCRAP);

        /**
         * The item used as filler for this cost level.
         */
        public final Item priceItem;

        private CopySmithingTemplateCostLevel(Item priceItem) {
            this.priceItem = priceItem;
        }
    }

    /**
     * A collection of ready-made recipe patterns for common block-family variants (stairs, slabs, walls,
     * buttons, pressure plates, roofs, and a few decorative shapes), used to cut down on boilerplate when a mod
     * registers a full set of variants for a base block.
     * <p>
     * Every {@code make...Recipe} method both builds and immediately writes ({@link BaseRecipeBuilder#build})
     * the resulting recipe(s) to the {@link #context} passed to the constructor.
     */
    public static class Templates {
        private static final String[] SHAPE_ROOF = new String[]{"# #", "###", " # "};
        private static final String[] SHAPE_STAIR = new String[]{"#  ", "## ", "###"};
        private static final String[] SHAPE_SLAB = new String[]{"###"};
        private static final String[] SHAPE_BUTTON = new String[]{"#"};
        private static final String[] SHAPE_PLATE = new String[]{"##"};
        private static final String[] SHAPE_X2 = new String[]{"##", "##"};
        private static final String[] SHAPE_3X2 = new String[]{"###", "###"};
        private static final String[] SHAPE_COLORING = new String[]{"###", "#I#", "###"};
        private static final String[] SHAPE_ROUND = new String[]{"###", "# #", "###"};
        private static final String[] SHAPE_FIRE_BOWL = new String[]{"#I#", " # ", "L L"};

        private final ModCore C;

        /**
         * The context every recipe built by this instance is written to.
         */
        public final RecipeBuilder.Context context;

        /**
         * Creates a new template helper.
         *
         * @param context The context to write all generated recipes to.
         * @param modCore The mod the generated recipe ids are namespaced under.
         */
        public Templates(RecipeBuilder.Context context, ModCore modCore) {
            this.C = modCore;
            this.context = context;
        }

        private void makeSingleRecipe(
                String group,
                Block source,
                Block result,
                String[] shape,
                int count,
                RecipeCategory category
        ) {

            String name = BuiltInRegistries.BLOCK
                    .getKey(source)
                    .getPath() + "_" + BuiltInRegistries.BLOCK
                    .getKey(result)
                    .getPath();

            RecipeBuilder
                    .crafting(C.id(name), result)
                    .outputCount(count)
                    .group(group)
                    .category(category)
                    .shape(shape)
                    .addMaterial('#', source)
                    .build(context);
        }

        /**
         * Builds and writes a 6-output crafting recipe that turns {@code source} into a roof tile block, using
         * the {@code roof_tile} recipe-book group.
         *
         * @param source The base block.
         * @param roof   The roof tile block to produce.
         */
        public void makeRoofRecipe(Block source, Block roof) {
            makeSingleRecipe("roof_tile", source, roof, SHAPE_ROOF, 6, RecipeCategory.BUILDING_BLOCKS);
        }

        /**
         * Builds and writes a 4-output crafting recipe plus a matching stonecutting recipe that turn
         * {@code source} into stairs, using the {@code stairs} recipe-book group.
         *
         * @param source The base block.
         * @param stairs The stairs block to produce.
         */
        public void makeStairsRecipe(Block source, Block stairs) {
            String name = BuiltInRegistries.BLOCK.getKey(stairs)
                                                 .getPath();

            makeSingleRecipe("stairs", source, stairs, SHAPE_STAIR, 4, RecipeCategory.BUILDING_BLOCKS);
            RecipeBuilder.stonecutting(C.id(name + "_stonecutting"), stairs)
                         .outputCount(1)
                         .input(source)
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .group("stairs")
                         .build(context);
        }


        /**
         * Builds and writes a 6-output crafting recipe plus a matching stonecutting recipe that turn
         * {@code source} into slabs, using the {@code slabs} recipe-book group.
         *
         * @param source The base block.
         * @param slab   The slab block to produce.
         */
        public void makeSlabRecipe(Block source, Block slab) {
            //String group = BuiltInRegistries.BLOCK.getKey(slab).getPath().contains("roof_tile") ? "roof_tile_slab" : slab.getSoundType(slab.defaultBlockState()) == SoundType.WOOD ? "nether_wooden_slab" : "nether_rock_slab";
            //woods are now registered through different means
            String name = BuiltInRegistries.BLOCK.getKey(slab)
                                                 .getPath();

            makeSingleRecipe("slabs", source, slab, SHAPE_SLAB, 6, RecipeCategory.BUILDING_BLOCKS);
            RecipeBuilder.stonecutting(C.id(name + "_stonecutting"), slab)
                         .outputCount(2)
                         .input(source)
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .group("slabs")
                         .build(context);
        }

        /**
         * Builds and writes a 1-output crafting recipe that turns {@code source} into a button, using the
         * {@code buttons} recipe-book group.
         *
         * @param source The base block.
         * @param button The button block to produce.
         */
        public void makeButtonRecipe(Block source, Block button) {
            makeSingleRecipe("buttons", source, button, SHAPE_BUTTON, 1, RecipeCategory.REDSTONE);
        }

        /**
         * Builds and writes a 1-output crafting recipe that turns {@code source} into a pressure plate, using
         * the {@code plates} recipe-book group.
         *
         * @param source The base block.
         * @param plate  The pressure plate block to produce.
         */
        public void makePlateRecipe(Block source, Block plate) {
            makeSingleRecipe("plates", source, plate, SHAPE_PLATE, 1, RecipeCategory.REDSTONE);
        }

        /**
         * Builds and writes a crafting recipe that turns a 2x2 square of {@code source} into {@code result}.
         *
         * @param source   The base block.
         * @param result   The block to produce.
         * @param count    The output stack size.
         * @param group    The recipe-book group.
         * @param category The recipe category.
         */
        public void makeSimpleRecipe2x2(
                Block source,
                Block result,
                int count,
                String group,
                RecipeCategory category
        ) {
            makeSingleRecipe(group, source, result, SHAPE_X2, count, category);
        }

        /**
         * Builds and writes a 6-output crafting recipe plus a matching stonecutting recipe that turn
         * {@code source} into a wall, using the {@code walls} recipe-book group.
         *
         * @param source The base block.
         * @param wall   The wall block to produce.
         */
        public void makeWallRecipe(Block source, Block wall) {
            String name = BuiltInRegistries.BLOCK.getKey(wall).getPath();

            RecipeBuilder
                    .crafting(C.id(name), wall)
                    .outputCount(6)
                    .group("walls")
                    .shape(SHAPE_3X2)
                    .category(RecipeCategory.DECORATIONS)
                    .addMaterial('#', source)
                    .build(context);

            RecipeBuilder.stonecutting(C.id(name + "_stonecutting"), wall)
                         .input(source)
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .group("walls")
                         .build(context);
        }

        /**
         * Builds and writes an 8-output crafting recipe that dyes {@code source} into {@code result} using a
         * ring of the source block around a central dye item.
         *
         * @param source   The base block.
         * @param result   The dyed block to produce.
         * @param dye      The dye item placed in the recipe's center slot.
         * @param group    The recipe-book group.
         * @param category The recipe category.
         */
        public void makeColoringRecipe(
                Block source,
                Block result,
                Item dye,
                String group,
                RecipeCategory category
        ) {
            String name = BuiltInRegistries.BLOCK.getKey(result).getPath();

            RecipeBuilder
                    .crafting(C.id(name), result)
                    .outputCount(8)
                    .group(group)
                    .category(category)
                    .shape(SHAPE_COLORING)
                    .addMaterial('#', source)
                    .addMaterial('I', dye)
                    .build(context);
        }

        /**
         * Builds and writes a crafting recipe that arranges {@code source} in a hollow ring to produce
         * {@code result}.
         *
         * @param source   The base block.
         * @param result   The block to produce.
         * @param group    The recipe-book group.
         * @param category The recipe category.
         */
        public void makeRoundRecipe(Block source, Block result, String group, RecipeCategory category) {
            String name = BuiltInRegistries.BLOCK.getKey(result).getPath();

            RecipeBuilder
                    .crafting(C.id(name), result)
                    .group(group)
                    .category(category)
                    .shape(SHAPE_ROUND)
                    .addMaterial('#', source)
                    .build(context);
        }

        /**
         * Builds and writes a crafting recipe for a "fire bowl" style block: a ring of {@code material} around
         * an {@code inside} filling, standing on a pair of {@code leg} items.
         *
         * @param material The material forming the bowl's rim.
         * @param inside   The block filling the bowl.
         * @param leg      The item forming the bowl's legs.
         * @param result   The block to produce.
         */
        public void makeFireBowlRecipe(Block material, Block inside, Item leg, Block result) {
            String name = BuiltInRegistries.BLOCK.getKey(result).getPath();

            RecipeBuilder
                    .crafting(C.id(name), result)
                    .group("fire_bowl")
                    .shape(SHAPE_FIRE_BOWL)
                    .addMaterial('#', material)
                    .addMaterial('I', inside)
                    .addMaterial('L', leg)
                    .category(RecipeCategory.DECORATIONS)
                    .build(context);
        }
    }

}
