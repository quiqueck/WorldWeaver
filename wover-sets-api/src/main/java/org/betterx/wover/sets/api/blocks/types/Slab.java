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
import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.SlabBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a slab slot: a {@link SlabBlock} carrying {@link BlockTraits#SLAB_BLOCK}, with a vanilla-style slab
 * model and an auto-generated recipe from a source block (with a matching stonecutting recipe for non-wood
 * materials).
 */
public class Slab extends SlotFromDefinition {
    protected final @Nullable SlotType baseBlockType;

    /**
     * Creates a factory for the {@link SlotType#SLAB} slot, slabbing the set's base block.
     */
    public Slab() {
        this(null, SlotType.SLAB);
    }

    /**
     * @param baseBlockType the slot (with fallback to the set's base slot, then vanilla stone) this slab is cut
     *                      from, or {@code null} to always use the set's base block
     * @param slot          the slot to register this slab under
     */
    public Slab(@Nullable SlotType baseBlockType, @NotNull SlotType slot) {
        super(slot);
        this.baseBlockType = baseBlockType;
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            String name
    ) {
        return registry.defineDefaultBlockWithProps(name, SlabBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.SLAB_BLOCK.withDefault());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.slab(baseBlockType == null
                ? set::getBaseBlock
                : () -> set.getBlockWithFallback(baseBlockType)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        final boolean wood = blockTraitLookup.hasTrait(BlockTraits.WOOD_BLOCK);
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.slab(
                baseBlockType == null
                        ? set.recipeBaseMaterial()
                        : set.recipeMaterialWithFallback(baseBlockType),
                wood ? "wooden_slab" : "slab",
                !wood
        );
    }
}
