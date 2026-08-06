package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;


/**
 * Builds a bookshelf slot: a plain block carrying {@link BlockTraits#BOOK_SHELF}, with a vanilla-style bookshelf
 * model (using the set's base block as the plank texture source) and an auto-generated recipe from the set's
 * base block.
 */
public class Bookshelf extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#BOOKSHELF} slot.
     */
    public Bookshelf() {
        this(SlotType.BOOKSHELF);
    }

    /**
     * @param slot the slot to register this bookshelf under
     */
    public Bookshelf(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BOOK_SHELF.withDefault());
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.bookshelf(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.bookshelf(set.recipeBaseMaterial());
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Vanilla tags no workstation or furnishing at all - not the crafting table, barrel, bookshelf,
        // composter, furnace or chest - only building materials, terrain, ores and a handful of decorative
        // full cubes. Bookshelf is a furnishing, so it drops the archetype its material would give it.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
