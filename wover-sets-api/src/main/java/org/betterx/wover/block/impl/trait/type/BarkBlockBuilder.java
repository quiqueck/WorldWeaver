package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;
import org.betterx.wover.block.api.trait.type.BarkBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BarkBlockBuilder extends AbstractBlockTraitBuilder.Generic implements BarkBlockTrait.Builder {
    public static final BarkBlockTrait.Builder BUILDER = new BarkBlockBuilder();

    private BarkBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_bark"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
    }

    public @Nullable List<BlockTrait<?, ?>> with(Block strippedBlockState) {
        if (strippedBlockState == null) return withDefault();
        return with((oldState) -> strippedBlockState.defaultBlockState());
    }

    public @Nullable List<BlockTrait<?, ?>> with(BlockState strippedBlockState) {
        if (strippedBlockState == null) return withDefault();
        return with((oldState) -> strippedBlockState);
    }

    public @Nullable List<BlockTrait<?, ?>> with(@Nullable StripableBlockTrait.BlockStateFactory strippedBlockState) {
        if (strippedBlockState == null) return withDefault();

        //Make sure we copy the axis property if it exists
        strippedBlockState = StripableBlockTrait.copyRotatedPillarBlockState(strippedBlockState);

        if (!ModCore.isDatagen())
            return combine(BlockTraits.STRIPABLE.with(strippedBlockState));
        return combine(new Trait(), BlockTraits.STRIPABLE.with(strippedBlockState), BlockTraits.LOOT_TABLE.dropSelf());
    }

    private class Trait extends BlockTraitImpl.Generic implements BarkBlockTrait {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(BlockTags.LOGS);
            definition.addItemTags(ItemTags.LOGS);

            if (definition.hasTrait(BlockTraits.FLAMMABLE)) {
                definition.addTags(BlockTags.LOGS_THAT_BURN);
            }
        }
    }
}