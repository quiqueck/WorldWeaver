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
import net.minecraft.world.level.block.CraftingTableBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a crafting table slot: a {@link CraftingTableBlock} carrying
 * {@link BlockTraits#CRAFTING_TABLE_BLOCK}, with a vanilla-style crafting-table-like model (using the set's base
 * block for the top/side/bottom textures) and an auto-generated recipe from the set's base block.
 */
public class CraftingTable extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#CRAFTING_TABLE} slot.
     */
    public CraftingTable() {
        this(SlotType.CRAFTING_TABLE);
    }

    /**
     * @param slot the slot to register this crafting table under
     */
    public CraftingTable(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(name, CraftingTableBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.CRAFTING_TABLE_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.craftingTable(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.craftingTable(set.recipeBaseMaterial());
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Vanilla tags no workstation or furnishing at all - not the crafting table, barrel, bookshelf,
        // composter, furnace or chest - only building materials, terrain, ores and a handful of decorative
        // full cubes. Crafting table is a workstation, so it drops the archetype its material would give it.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
