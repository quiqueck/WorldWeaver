package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotDefinition;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class Plank extends WoodenSlotDefinition {
    public Plank(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.PLANK_BLOCK.withDefault());
    }

    @Override
    protected void buildRecipe(
            WoodenBlockSet<?> set,
            ResourceKey<Block> key,
            Block block,
            RecipeBuilder.Context context
    ) {
        RecipeBuilder.crafting(key.location(), block)
                     .outputCount(4)
                     .shapeless()
                     .addMaterial('#', set.logsItemTag)
                     .group("planks")
                     .category(RecipeCategory.BUILDING_BLOCKS)
                     .build(context);
    }
}