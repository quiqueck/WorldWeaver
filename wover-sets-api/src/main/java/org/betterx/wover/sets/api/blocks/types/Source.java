package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Source extends SlotFromDefinition {
    protected final @Nullable SlotType baseBlockType;

    public Source() {
        this(null, SlotType.SOURCE);
    }

    public Source(@Nullable SlotType baseBlockType, @NotNull SlotType slot) {
        super(slot);
        this.baseBlockType = baseBlockType;
    }


    @Override
    public String getName(BlockSet<?> set) {
        if (slot == SlotType.SOURCE) {
            return set.baseName;
        }
        return super.getName(set);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.cube(baseBlockType == null
                ? set::getBaseBlock
                : () -> set.getBlockWithFallback(baseBlockType)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        var sourceMaterial = baseBlockType == null
                ? set.recipeBaseMaterial()
                : set.recipeMaterialWithFallback(baseBlockType);

        if (slot == SlotType.BRICK) {
            return RecipeTraitLibrary.brickSource(sourceMaterial, true);
        } else if (slot == SlotType.CRACKED) {
            return RecipeTraitLibrary.crackedSource(sourceMaterial, false);
        } else if (slot == SlotType.CHISELED) {
            return RecipeTraitLibrary.stoneCutSource(sourceMaterial);
        } else if (slot == SlotType.POLISHED) {
            return RecipeTraitLibrary.stoneCutSource(sourceMaterial);
        } else if (slot == SlotType.WEATHERED) {
            return RecipeTraitLibrary.mossySource(sourceMaterial);
        }

        // If the slot is the original source block, then there is no default recipe
        return null;
    }
}
