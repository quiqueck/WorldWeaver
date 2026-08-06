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
import net.minecraft.world.level.block.StairBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a stairs slot: a {@link StairBlock} using the base state of a source block, carrying
 * {@link BlockTraits#STAIR_BLOCK}, with a vanilla-style stairs model and an auto-generated recipe from a source
 * block (with a matching stonecutting recipe for non-wood materials).
 */
public class Stairs extends SlotFromDefinition {
    protected final @Nullable SlotType baseBlockType;

    /**
     * Creates a factory for the {@link SlotType#STAIRS} slot, cut from the set's base block.
     */
    public Stairs() {
        this(null, SlotType.STAIRS);
    }

    /**
     * @param baseBlockType the slot (with fallback to the set's base slot, then vanilla stone) these stairs are
     *                      cut from, or {@code null} to always use the set's base block
     * @param slot          the slot to register these stairs under
     */
    public Stairs(@Nullable SlotType baseBlockType, @NotNull SlotType slot) {
        super(slot);
        this.baseBlockType = baseBlockType;
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(
                name,
                (props) -> new StairBlock(
                        (baseBlockType == null ? set.getBaseBlock() : set.getBlockWithFallback(baseBlockType))
                                .defaultBlockState(),
                        props
                )
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.STAIR_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.stairs(baseBlockType == null
                ? set::getBaseBlock
                : () -> set.getBlockWithFallback(baseBlockType)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        final boolean wood = blockTraitLookup.hasTrait(BlockTraits.WOOD_BLOCK);
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.stairs(
                baseBlockType == null
                        ? set.recipeBaseMaterial()
                        : set.recipeMaterialWithFallback(baseBlockType),
                wood ? "wooden_stairs" : "stairs",
                !wood
        );
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a stepped block), so it must not inherit the set material's sulfur cube
        // archetype: vanilla lists no stairs-shaped block in any archetype tag, and a cube renders what it
        // swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
