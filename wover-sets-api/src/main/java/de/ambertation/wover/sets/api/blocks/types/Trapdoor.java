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
import net.minecraft.world.level.block.TrapDoorBlock;


import org.jetbrains.annotations.NotNull;

/**
 * Builds a trapdoor slot: a {@link TrapDoorBlock} using the set's {@link BlockSet#setType()}, carrying
 * {@link BlockTraits#TRAPDOOR_BLOCK}, with a vanilla-style orientable trapdoor model and an auto-generated
 * recipe from the set's base block, in the matching {@code wooden_trapdoor}/{@code trapdoor} recipe group.
 */
public class Trapdoor extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#TRAPDOOR} slot.
     */
    public Trapdoor() {
        this(SlotType.TRAPDOOR);
    }

    /**
     * @param slot the slot to register this trapdoor under
     */
    public Trapdoor(SlotType slot) {
        super(slot);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(
                name,
                (props) -> new TrapDoorBlock(set.setType(), props)
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.TRAPDOOR_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.orientableTrapdoor();
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        final boolean wood = blockTraitLookup.hasTrait(BlockTraits.WOOD_BLOCK);
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.trapdoor(
                set.recipeBaseMaterial(),
                wood ? "wooden_trapdoor" : "trapdoor"
        );
    }
}