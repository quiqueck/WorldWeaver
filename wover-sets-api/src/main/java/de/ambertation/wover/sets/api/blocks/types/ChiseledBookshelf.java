package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.SoundType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#CHISELED_BOOKSHELF} slot: a vanilla {@link ChiseledBookShelfBlock} carrying
 * {@link BlockTraits#CHISELED_BOOK_SHELF}, with a vanilla-style chiseled bookshelf model and the vanilla
 * planks-and-slabs recipe.
 * <p>
 * A set adding this slot has to ship four textures for it: {@code <name>_chiseled_bookshelf_top},
 * {@code _side}, {@code _empty} and {@code _occupied} (the last two are the front face's six book slots, empty
 * and filled).
 */
public class ChiseledBookshelf extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#CHISELED_BOOKSHELF} slot.
     */
    public ChiseledBookshelf() {
        this(SlotType.CHISELED_BOOKSHELF);
    }

    /**
     * @param slot the slot to register this chiseled bookshelf under
     */
    public ChiseledBookshelf(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(name, ChiseledBookShelfBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.CHISELED_BOOK_SHELF.withDefault());
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.chiseledBookshelf();
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return RecipeTraitLibrary.chiseledBookshelf(
                set.recipeBaseMaterial(),
                set.recipeMaterial(SlotType.SLAB)
        );
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Vanilla's chiseled bookshelf is softer than the rest of the wood family (strength 1.5 rather than
        // WOOD_BLOCK's 2.0/3.0) and has its own sound. Both have to land after the set's common
        // configuration - see SlotFromDefinition#finalizeDefinitions on call ordering.
        def.strength(1.5F).sound(SoundType.CHISELED_BOOKSHELF);

        // Vanilla tags no workstation or furnishing at all - not the crafting table, barrel, bookshelf,
        // composter, furnace or chest - only building materials, terrain, ores and a handful of decorative
        // full cubes. A chiseled bookshelf is a furnishing, so it drops the archetype its material would give it.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
