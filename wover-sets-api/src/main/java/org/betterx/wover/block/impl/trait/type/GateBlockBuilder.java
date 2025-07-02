package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class GateBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefault {
    public static final GenericBlockTrait.BuilderWithDefault BUILDER = new GateBlockBuilder();

    private GateBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_gate"));
    }

    public @Nullable BlockTrait<?, ?> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return new Trait();
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.noOcclusion();

            definition.addTags(BlockTags.FENCE_GATES);
            definition.addItemTags(ItemTags.FENCE_GATES);
        }
    }
}
