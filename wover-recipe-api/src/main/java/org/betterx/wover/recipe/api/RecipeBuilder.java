package org.betterx.wover.recipe.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.recipe.impl.*;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class RecipeBuilder {
    public static Event<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            RecipeRuntimeProviderImpl.BOOTSTRAP_RECIPES;

    public static CraftingRecipeBuilder crafting(ResourceLocation id, ItemLike output) {
        return new CraftingRecipeBuilderImpl(id, output);
    }

    public static StonecutterRecipeBuilder stonecutting(ResourceLocation id, ItemLike output) {
        return new StonecutterRecipeBuilderImpl(id, output);
    }

    public static SmithingRecipeBuilder smithing(ResourceLocation id, ItemLike output) {
        return new SmithingRecipeBuilderImpl(id, output);
    }

    public static CookingRecipeBuilder cooking(ResourceLocation id, ItemLike output) {
        return new CookingRecipeBuilderImpl(id, output, false, false, false, false);
    }

    public static CookingRecipeBuilder cookableFood(
            ResourceLocation id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, true, true, false);
    }

    public static CookingRecipeBuilder smelting(ResourceLocation id, ItemLike output) {
        return new CookingRecipeBuilderImpl(id, output, false, false, false, true);
    }

    public static CookingRecipeBuilder blasting(
            ResourceLocation id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, true, false, false, true);
    }

    public static CookingRecipeBuilder smoker(
            ResourceLocation id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, false, true, false);
    }

    public static CookingRecipeBuilder campfire(
            ResourceLocation id,
            ItemLike output
    ) {
        return new CookingRecipeBuilderImpl(id, output, false, true, false, false);
    }

    private static CraftingRecipeBuilder copySmithingTemplateBase(
            ResourceLocation id,
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

    public static CraftingRecipeBuilder copySmithingTemplate(
            ResourceLocation id,
            CopySmithingTemplateCostLevel costLevel,
            ItemLike outputTemplate,
            ItemLike centerIngredient
    ) {
        return copySmithingTemplateBase(id, costLevel.priceItem, outputTemplate).addMaterial('C', centerIngredient);
    }

    public static CraftingRecipeBuilder copySmithingTemplate(
            ResourceLocation id,
            CopySmithingTemplateCostLevel costLevel,
            ItemLike outputTemplate,
            TagKey<Item> centerIngredient
    ) {
        return copySmithingTemplateBase(id, costLevel.priceItem, outputTemplate).addMaterial('C', centerIngredient);
    }

    public enum CopySmithingTemplateCostLevel {
        CHEAP(Items.STICK), REGULAR(Items.DIAMOND), EXPENSIVE(Items.NETHERITE_SCRAP);

        public final Item priceItem;

        private CopySmithingTemplateCostLevel(Item priceItem) {
            this.priceItem = priceItem;
        }
    }

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
        public final RecipeOutput context;
        public final HolderGetter<Item> items;
        public final RecipeProvider provider;

        public Templates(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput context, ModCore modCore) {
            this.C = modCore;
            this.context = context;
            this.items = items;
            this.provider = provider;
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
                    .build(items, provider, context);
        }

        public void makeRoofRecipe(Block source, Block roof) {
            makeSingleRecipe("roof_tile", source, roof, SHAPE_ROOF, 6, RecipeCategory.BUILDING_BLOCKS);
        }

        public void makeStairsRecipe(Block source, Block stairs) {
            String name = BuiltInRegistries.BLOCK.getKey(stairs)
                                                 .getPath();

            makeSingleRecipe("stairs", source, stairs, SHAPE_STAIR, 4, RecipeCategory.BUILDING_BLOCKS);
            RecipeBuilder.stonecutting(C.id(name + "_stonecutting"), stairs)
                         .outputCount(1)
                         .input(source)
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .group("stairs")
                         .build(items, provider, context);
        }


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
                         .build(items, provider, context);
        }

        public void makeButtonRecipe(Block source, Block button) {
            makeSingleRecipe("buttons", source, button, SHAPE_BUTTON, 1, RecipeCategory.REDSTONE);
        }

        public void makePlateRecipe(Block source, Block plate) {
            makeSingleRecipe("plates", source, plate, SHAPE_PLATE, 1, RecipeCategory.REDSTONE);
        }

        public void makeSimpleRecipe2x2(
                Block source,
                Block result,
                int count,
                String group,
                RecipeCategory category
        ) {
            makeSingleRecipe(group, source, result, SHAPE_X2, count, category);
        }

        public void makeWallRecipe(Block source, Block wall) {
            String name = BuiltInRegistries.BLOCK.getKey(wall).getPath();

            RecipeBuilder
                    .crafting(C.id(name), wall)
                    .outputCount(6)
                    .group("walls")
                    .shape(SHAPE_3X2)
                    .category(RecipeCategory.DECORATIONS)
                    .addMaterial('#', source)
                    .build(items, provider, context);

            RecipeBuilder.stonecutting(C.id(name + "_stonecutting"), wall)
                         .input(source)
                         .category(RecipeCategory.BUILDING_BLOCKS)
                         .group("walls")
                         .build(items, provider, context);
        }

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
                    .build(items, provider, context);
        }

        public void makeRoundRecipe(Block source, Block result, String group, RecipeCategory category) {
            String name = BuiltInRegistries.BLOCK.getKey(result).getPath();

            RecipeBuilder
                    .crafting(C.id(name), result)
                    .group(group)
                    .category(category)
                    .shape(SHAPE_ROUND)
                    .addMaterial('#', source)
                    .build(items, provider, context);
        }

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
                    .build(items, provider, context);
        }
    }

}
