package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookshelfBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ChiseledBookshelfBlockBuilder();

    private final List<BlockTrait<?, ?>> DEFAULT;

    private ChiseledBookshelfBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_chiseled_bookshelf"));
        DEFAULT = combine(
                new Trait(),
                BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.CHISELED_BOOKSHELF),
                // Vanilla's chiseled bookshelf is silk-touch-only, with no consolation drop
                // (VanillaBlockLoot#dropWhenSilkTouch) - unlike the plain bookshelf, which gives back 3 books.
                BlockTraits.LOOT_TABLE.silkTouchSelf()
        );
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        return DEFAULT;
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            // Deliberately no MAGIC_SOURCE and no #c:bookshelves: a chiseled bookshelf stores books, it does
            // not power an enchanting table, and vanilla keeps it out of every bookshelf tag for that reason.
            // The only tag vanilla gives it is mineable/axe, which WOOD_BLOCK already contributes.
        }
    }
}
