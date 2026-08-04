package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.loot.api.LootLookupProvider;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BookshelfBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new BookshelfBlockBuilder();

    private BookshelfBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_bookshelf"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(
                new Trait(),
                BlockTraits.MAGIC_SOURCE.withDefault(),
                BlockTraits.LOOT_TABLE.with(BookshelfBlockBuilder::drops)
        );
    }

    private static LootTable.Builder drops(
            ResourceKey<LootTable> tableKey,
            ResourceKey<Block> blockKey,
            Block block,
            LootLookupProvider provider
    ) {
        return provider.dropWithSilkTouch(block, Items.BOOK, ConstantValue.exactly(3));
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(CommonBlockTags.BOOKSHELVES);
        }
    }
}
