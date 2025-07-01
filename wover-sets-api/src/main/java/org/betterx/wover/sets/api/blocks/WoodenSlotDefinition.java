package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.TraitLookup;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildModel(BlockSet<?> set, TraitLookup traitLookup) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            return buildWoodModel(woodenSet, traitLookup);
        } else {
            throw new IllegalArgumentException("WoodenSlotDefinition can only be used with WoodenBlockSet");
        }
    }

    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildWoodModel(WoodenBlockSet<?> set, TraitLookup traitLookup) {
        return null;
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, TraitLookup traitLookup) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            return buildWoodRecipe(woodenSet, traitLookup);
        } else {
            throw new IllegalArgumentException("WoodenSlotDefinition can only be used with WoodenBlockSet");
        }
    }

    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, TraitLookup traitLookup) {
        return null;
    }
}
