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

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a "source" (plain full-block) slot of a stone-like set: a plain block dropping itself
 * ({@link BlockTraits#LOOT_TABLE}{@code .dropSelf()}), with a plain cube model. For {@link SlotType#SOURCE}
 * itself, the block is named after the set's {@code baseName} directly (no suffix) and no recipe is generated -
 * it is meant to be the set's raw material. Named variants (brick/cracked/chiseled/polished/tiles/weathered, via
 * the second constructor) instead generate a matching crafting/blasting/stonecutting recipe from a source
 * material.
 */
public class Source extends SlotFromDefinition {
    protected final @Nullable SlotType baseBlockType;

    /**
     * Creates a factory for the {@link SlotType#SOURCE} slot itself (no recipe generated).
     */
    public Source() {
        this(null, SlotType.SOURCE);
    }

    /**
     * @param baseBlockType the slot (with fallback to the set's base slot, then vanilla stone) this variant is
     *                      derived from, or {@code null} to always use the set's base block
     * @param slot          the slot to register this variant under; recognized values with a generated recipe
     *                      are {@link SlotType#BRICK}, {@link SlotType#CRACKED}, {@link SlotType#CHISELED},
     *                      {@link SlotType#POLISHED}, {@link SlotType#TILES}, and {@link SlotType#WEATHERED}
     */
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
        if (slot == SlotType.BRICK) {
            def.addTags(BlockTags.STONE_BRICKS);
            def.addItemTags(ItemTags.STONE_BRICKS);
        }
        def.addTrait(BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.cube(
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
        } else if (slot == SlotType.TILES) {
            return RecipeTraitLibrary.stoneCutSource(sourceMaterial);
        } else if (slot == SlotType.WEATHERED) {
            return RecipeTraitLibrary.mossySource(sourceMaterial);
        }

        // If the slot is the original source block, then there is no default recipe
        return null;
    }
}
