package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait;
import de.ambertation.wover.block.api.trait.type.LogBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class LogBlockBuilder extends AbstractBlockTraitBuilder.Generic implements LogBlockTrait.Builder {
    public static final LogBlockTrait.Builder BUILDER = new LogBlockBuilder();

    private LogBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_log"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
    }

    public @Nullable List<BlockTrait<?, ?>> with(@Nullable Block strippedBlockState) {
        if (strippedBlockState == null) return withDefault();
        return with((oldState) -> strippedBlockState.defaultBlockState());
    }

    public @Nullable List<BlockTrait<?, ?>> with(@Nullable BlockState strippedBlockState) {
        if (strippedBlockState == null) return withDefault();
        return with((oldState) -> strippedBlockState);
    }

    public @Nullable List<BlockTrait<?, ?>> with(@Nullable StripableBlockTrait.BlockStateFactory strippedBlockState) {
        if (strippedBlockState == null) return withDefault();
        //Make sure we copy the axis property if it exists
        strippedBlockState = StripableBlockTrait.copyRotatedPillarBlockState(strippedBlockState);

        if (!ModCore.isDatagen()) return combine(BlockTraits.STRIPABLE.with(strippedBlockState));
        return combine(new Trait(), BlockTraits.STRIPABLE.with(strippedBlockState), BlockTraits.LOOT_TABLE.dropSelf());
    }


    private class Trait extends BlockTraitImpl.Generic implements LogBlockTrait {
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