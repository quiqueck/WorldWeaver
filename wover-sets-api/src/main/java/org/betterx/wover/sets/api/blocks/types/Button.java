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
import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.ButtonBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Button extends SlotDefinition {
    public Button() {
        this(SlotType.BUTTON);
    }

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

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
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
}
