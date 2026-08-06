package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ShelfBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ShelfBlockBuilder();

    private final List<BlockTrait<?, ?>> DEFAULT;

    private ShelfBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_shelf"));
        DEFAULT = combine(
                new Trait(),
                BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityTypes.SHELF),
                // Vanilla's shelves drop plainly, without copying the block entity's contents (see
                // loot_table/blocks/oak_shelf.json) - a broken shelf spills its items into the world instead.
                BlockTraits.LOOT_TABLE.dropSelf()
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
            definition.noOcclusion();

            if (ModCore.isDatagen()) {
                // Not decoration: ShelfBlock#isConnectableShelf gates the whole side-chain (the left/center/
                // right models a powered row of shelves connects into) on #minecraft:wooden_shelves, and
                // vanilla's #minecraft:mineable/axe and the shelf's 300-tick furnace fuel value both come in
                // through this tag as well.
                definition.addTags(BlockTags.WOODEN_SHELVES);
                definition.addItemTags(ItemTags.WOODEN_SHELVES);
            }
        }
    }
}
