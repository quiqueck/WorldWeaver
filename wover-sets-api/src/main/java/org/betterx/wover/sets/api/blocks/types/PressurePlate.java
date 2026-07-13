package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.PressurePlateBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.pressurePlate(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.pressurePlate(set.recipeBaseMaterial());
    }
}
