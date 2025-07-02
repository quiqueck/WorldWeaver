package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class HangingSignBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new HangingSignBlockBuilder();

    private HangingSignBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_hanging_sign"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.HANGING_SIGN));
        return combine(new Trait(), BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.HANGING_SIGN));
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            if ((Object) definition instanceof WallSignBlockDefinition wall) {
                definition.addTags(BlockTags.WALL_HANGING_SIGNS);
            } else {
                definition.addTags(BlockTags.CEILING_HANGING_SIGNS);
            }

            definition.forceSolidOn()
                      .instrument(NoteBlockInstrument.BASS)
                      .noCollission()
                      .strength(1.0F);
        }
    }
}
