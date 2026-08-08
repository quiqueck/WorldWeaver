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

import java.util.function.BiConsumer;
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
    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        // Vanilla's chiseled bookshelf is softer than the rest of the wood family (strength 1.5 rather
        // than WOOD_BLOCK's 2.0/3.0) and has its own sound. Setters and traits are applied in call
        // order, so both only win from here, the last point before the block is built. (This branch has
        // no finalizeDefinitions hook, and no SULFUR_CUBE_ARCHETYPE trait to opt out of either.)
        definition.strength(1.5F).sound(SoundType.CHISELED_BOOKSHELF);

        super.finalizeBlockDefinitions(set, definition, blockDefinitionConsumer);
    }
}
