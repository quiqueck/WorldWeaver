package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;

public class WoodenSlotDefinition extends SlotDefinition {
    protected WoodenSlotDefinition(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            addWoodSlotSpecificDefinitions(woodenSet, def);
        } else {
            throw new IllegalArgumentException("WoodenSlotDefinition can only be used with WoodenBlockSet");
        }
    }

    protected void addWoodSlotSpecificDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {

    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            return buildWoodRecipe(woodenSet);
        } else {
            throw new IllegalArgumentException("WoodenSlotDefinition can only be used with WoodenBlockSet");
        }
    }

    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set) {
        return null;
    }
}
