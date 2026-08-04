package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.WoodenSlotFromDefinition;

import net.minecraft.world.level.block.Block;


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

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.planks();
    }

    @Override
    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.planks(RecipeMaterial.ofDeferredTag(() -> set.logsItemTag));
    }
}