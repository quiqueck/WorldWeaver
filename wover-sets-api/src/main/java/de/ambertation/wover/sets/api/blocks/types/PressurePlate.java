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
import net.minecraft.world.level.block.PressurePlateBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a pressure plate slot: a {@link PressurePlateBlock} using the set's {@link BlockSet#setType()}, carrying
 * {@link BlockTraits#PRESSURE_PLATE_BLOCK}, with a vanilla-style pressure plate model and an auto-generated
 * recipe from the set's base block.
 */
public class PressurePlate extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#PRESSURE_PLATE} slot.
     */
    public PressurePlate() {
        this(SlotType.PRESSURE_PLATE);
    }

    /**
     * @param slot the slot to register this pressure plate under
     */
    public PressurePlate(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(name, prop -> new PressurePlateBlock(set.setType(), prop));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.PRESSURE_PLATE_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.pressurePlate(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.pressurePlate(set.recipeBaseMaterial());
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a flat plate on the floor), so it must not inherit the set material's sulfur
        // cube archetype: vanilla lists no pressure plate-shaped block in any archetype tag, and a cube
        // renders what it swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
