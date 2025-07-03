package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class StairsBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new StairsBlockBuilder();

    private StairsBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_stairs"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(BlockTags.STAIRS);
            definition.addItemTags(ItemTags.STAIRS);

            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(BlockTags.WOODEN_STAIRS);
                definition.addItemTags(ItemTags.WOODEN_STAIRS);
            }
        }
    }
}