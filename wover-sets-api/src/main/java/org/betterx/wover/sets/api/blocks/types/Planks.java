package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotFromDefinition;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builds the planks slot of a {@link WoodenBlockSet}: a plain block carrying {@link BlockTraits#PLANK_BLOCK},
 * with a plain cube model and an auto-generated recipe from the set's shared logs item tag
 * ({@link WoodenBlockSet#logsItemTag}), so planks can be crafted from any log/stem variant.
 */
public class Planks extends WoodenSlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#PLANKS} slot.
     */
    public Planks() {
        this(SlotType.PLANKS);
    }

    /**
     * @param slot the slot to register these planks under
     */
    public Planks(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.PLANK_BLOCK);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.planks();
    }

    @Override
    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.planks(RecipeMaterial.ofDeferredTag(() -> set.logsItemTag));
    }
}