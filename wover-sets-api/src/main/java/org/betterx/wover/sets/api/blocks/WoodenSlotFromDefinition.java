package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A {@link SlotFromDefinition} variant for slots that only make sense on a {@link WoodenBlockSet} (e.g. planks,
 * gates, log/bark). Restricts {@link #addSlotSpecificDefinitions}/{@link #buildModel}/{@link #buildRecipe} to
 * {@link WoodenBlockSet}-typed overloads ({@link #addWoodSlotSpecificDefinitions}/{@link #buildWoodModel}/
 * {@link #buildWoodRecipe}), throwing if used on a plain {@link BlockSet}. See
 * {@code org.betterx.wover.sets.api.blocks.types.Planks}/{@code Gate}/{@code Log}/{@code Bark} for concrete
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
    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
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
     * @return the model trait, or {@code null} for none
     */
    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildWoodModel(WoodenBlockSet<?> set, BlockTraitLookup blockTraitLookup) {
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
