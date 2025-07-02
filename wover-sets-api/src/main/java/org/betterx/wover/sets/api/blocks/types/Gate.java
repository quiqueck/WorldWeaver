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
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotDefinition;

import net.minecraft.world.level.block.FenceGateBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gate extends WoodenSlotDefinition {
    public Gate() {
        this(SlotType.GATE);
    }

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

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.gate(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.gate(set.recipeBaseMaterial());
    }
}
