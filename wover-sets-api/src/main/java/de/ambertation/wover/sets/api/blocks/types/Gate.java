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
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.WoodenSlotFromDefinition;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a fence gate slot: a {@link FenceGateBlock} using the set's {@link WoodenBlockSet#woodType()}, carrying
 * {@link BlockTraits#FENCE_GATE_BLOCK}, with a vanilla-style gate model and an auto-generated recipe from the
 * set's base block. Only usable on a {@link WoodenBlockSet}.
 */
public class Gate extends WoodenSlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#GATE} slot.
     */
    public Gate() {
        this(SlotType.GATE);
    }

    /**
     * @param slot the slot to register this gate under
     */
    public Gate(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        if (set instanceof WoodenBlockSet<?> woodenBlockSet) {
            return registry.defineDefaultBlock(
                    name,
                    (def) -> new FenceGateBlock(woodenBlockSet.woodType(), def.getProperties())
            );
        } else {
            throw new IllegalArgumentException("Gate slot can only be used with WoodenBlockSet.");
        }
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.FENCE_GATE_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.gate(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.gate(set.recipeBaseMaterial());
    }
}
