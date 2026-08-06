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
import net.minecraft.world.level.block.DoorBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a door slot: a {@link DoorBlock} using the set's {@link BlockSet#setType()}, carrying
 * {@link BlockTraits#DOOR_BLOCK}, with a vanilla-style door model and an auto-generated recipe from the set's
 * base block.
 */
public class Door extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#DOOR} slot.
     */
    public Door() {
        this(SlotType.DOOR);
    }

    /**
     * @param slot the slot to register this door under
     */
    public Door(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, (def) -> new DoorBlock(set.setType(), def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.DOOR_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.door();
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.door(set.recipeBaseMaterial());
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a thin swinging panel), so it must not inherit the set material's sulfur cube
        // archetype: vanilla lists no door-shaped block in any archetype tag, and a cube renders what it
        // swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
