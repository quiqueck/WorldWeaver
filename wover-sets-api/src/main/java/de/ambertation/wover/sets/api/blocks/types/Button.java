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
import net.minecraft.world.level.block.ButtonBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a button slot: a {@link ButtonBlock} carrying {@link BlockTraits#BUTTON_BLOCK}, with a
 * material-appropriate press duration (20 ticks for stone, 30 for wood, 8 otherwise), a vanilla-style button
 * model, and an auto-generated recipe in the matching {@code wooden_button}/{@code stone_button} recipe group.
 */
public class Button extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#BUTTON} slot.
     */
    public Button() {
        this(SlotType.BUTTON);
    }

    /**
     * @param slot the slot to register this button under
     */
    public Button(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set, @NotNull String name
    ) {
        return registry.defineDefaultBlock(
                name, (def) -> {
                    int ticksToStayPressed = 8;
                    if (def.hasTrait(BlockTraits.STONE_BLOCK)) {
                        ticksToStayPressed = 20;
                    } else if (def.hasTrait(BlockTraits.WOOD_BLOCK)) {
                        ticksToStayPressed = 30;
                    }
                    return new ButtonBlock(set.setType(), ticksToStayPressed, def.getProperties());
                }
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BUTTON_BLOCK.withDefault());
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.button(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        String group = "button";
        if (blockTraitLookup.hasTrait(BlockTraits.STONE_BLOCK)) {
            group = "stone_button";
        } else if (blockTraitLookup.hasTrait(BlockTraits.WOOD_BLOCK)) {
            group = "wooden_button";
        }

        return RecipeTraitLibrary.button(set.recipeBaseMaterial(), group);
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a tiny nub on a face), so it must not inherit the set material's sulfur cube
        // archetype: vanilla lists no button-shaped block in any archetype tag, and a cube renders what it
        // swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
