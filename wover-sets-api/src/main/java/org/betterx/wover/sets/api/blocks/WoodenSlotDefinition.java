package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class WoodenSlotDefinition extends SlotDefinition {
    protected WoodenSlotDefinition(SlotType slot) {
        super(slot);
    }

    @Override
    protected final void buildRecipe(
            BlockSet<?> set,
            ResourceKey<Block> key,
            Block block,
            RecipeBuilder.Context context
    ) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            buildRecipe(woodenSet, key, block, context);
        } else {
            throw new IllegalArgumentException("WoodenSlotDefinition can only be used with WoodenBlockSet");
        }
    }

    protected void buildRecipe(
            WoodenBlockSet<?> set,
            ResourceKey<Block> key,
            Block block,
            RecipeBuilder.Context context
    ) {

    }
}
