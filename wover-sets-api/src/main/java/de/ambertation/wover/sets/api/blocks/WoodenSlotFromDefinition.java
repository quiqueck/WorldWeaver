package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;

import net.minecraft.world.level.block.Block;

/**
 * A {@link SlotFromDefinition} variant for slots that only make sense on a {@link WoodenBlockSet} (e.g. planks,
 * gates, log/bark). Restricts {@link #addSlotSpecificDefinitions}/{@link #buildModel}/{@link #buildRecipe} to
 * {@link WoodenBlockSet}-typed overloads ({@link #addWoodSlotSpecificDefinitions}/{@link #buildWoodModel}/
 * {@link #buildWoodRecipe}), throwing if used on a plain {@link BlockSet}. See
 * {@code de.ambertation.wover.sets.api.blocks.types.Planks}/{@code Gate}/{@code Log}/{@code Bark} for concrete
 * subclasses.
 */
public class WoodenSlotFromDefinition extends SlotFromDefinition {
    /**
     * @param slot the slot this factory fills
     */
    protected WoodenSlotFromDefinition(SlotType slot) {
        super(slot);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            addWoodSlotSpecificDefinitions(woodenSet, def);
        } else {
            throw new IllegalArgumentException("WoodenSlotFromDefinition can only be used with WoodenBlockSet");
        }
    }

    /**
     * Wood-typed equivalent of {@link #addSlotSpecificDefinitions}. The default implementation does nothing.
     *
     * @param set the wooden block set this factory belongs to
     * @param def the definition to configure
     */
    protected void addWoodSlotSpecificDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {

    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            finalizeWoodDefinitions(woodenSet, def);
        } else {
            throw new IllegalArgumentException("WoodenSlotFromDefinition can only be used with WoodenBlockSet");
        }
    }

    /**
     * Wood-typed equivalent of {@link #finalizeDefinitions}. The default implementation does nothing.
     *
     * @param set the wooden block set this factory belongs to
     * @param def the definition to configure
     */
    protected void finalizeWoodDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {

    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            return buildWoodModel(woodenSet, blockTraitLookup);
        } else {
            throw new IllegalArgumentException("WoodenSlotFromDefinition can only be used with WoodenBlockSet");
        }
    }

    /**
     * Wood-typed equivalent of {@link #buildModel}. The default implementation returns {@code null}.
     *
     * @param set              the wooden block set this factory belongs to
     * @param blockTraitLookup the block's trait lookup, to query traits added earlier during configuration
     * @return the model binding, or {@code null} for none
     */
    protected BlockTrait<Block, ?> buildWoodModel(WoodenBlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return null;
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        if (set instanceof WoodenBlockSet<?> woodenSet) {
            return buildWoodRecipe(woodenSet, blockTraitLookup);
        } else {
            throw new IllegalArgumentException("WoodenSlotFromDefinition can only be used with WoodenBlockSet");
        }
    }

    /**
     * Wood-typed equivalent of {@link #buildRecipe}. The default implementation returns {@code null}.
     *
     * @param set              the wooden block set this factory belongs to
     * @param blockTraitLookup the block's trait lookup, to query traits added earlier during configuration
     * @return the recipe trait, or {@code null} for none
     */
    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return null;
    }
}
