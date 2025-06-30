package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class Slab extends SlotDefinition {
    public Slab(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.SLAB_BLOCK.withDefault());
    }

    @Override
    protected void buildRecipe(BlockSet<?> set, ResourceKey<Block> key, Block block, RecipeBuilder.Context context) {
        RecipeBuilder
                .crafting(key.location(), block)
                .outputCount(6)
                .shape("###")
                .addMaterial('#', set.getBaseBlock())
                .group("slab")
                .category(RecipeCategory.BUILDING_BLOCKS)
                .build(context);
    }
}
