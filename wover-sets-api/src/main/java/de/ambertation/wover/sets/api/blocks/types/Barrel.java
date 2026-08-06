package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
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
import net.minecraft.world.level.block.BarrelBlock;


import org.jetbrains.annotations.NotNull;

/**
 * Builds the {@link SlotType#BARREL} slot: a {@link BarrelBlock} carrying {@link BlockTraits#BARREL_BLOCK}, with
 * a vanilla-style barrel model and an auto-generated recipe from the set's planks and slab.
 */
public class Barrel extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#BARREL} slot.
     */
    public Barrel() {
        super(SlotType.BARREL);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            String name
    ) {
        return registry.defineDefaultBlockWithProps(name, BarrelBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BARREL_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.barrel();
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return RecipeTraitLibrary.barrel(
                set.recipeBaseMaterial(),
                set.recipeMaterial(SlotType.SLAB)
        );
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Vanilla tags no workstation or furnishing at all - not the crafting table, barrel, bookshelf,
        // composter, furnace or chest - only building materials, terrain, ores and a handful of decorative
        // full cubes. Barrel is a workstation, so it drops the archetype its material would give it.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
