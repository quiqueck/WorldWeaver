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

public class GateBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new GateBlockBuilder();

    private final List<BlockTrait<?, ?>> DEFAULT;

    private GateBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_gate"));
        DEFAULT = combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
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
                definition.addTags(BlockTags.FENCE_GATES);
                definition.addItemTags(ItemTags.FENCE_GATES);
            }
        }
    }
}
