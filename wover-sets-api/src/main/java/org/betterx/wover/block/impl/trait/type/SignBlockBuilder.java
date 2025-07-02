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

public class SignBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new SignBlockBuilder();

    private SignBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_sign"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.SIGN));
        return combine(new Trait(), BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.SIGN));
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            if ((Object) definition instanceof WallSignBlockDefinition wall) {
                definition.addTags(BlockTags.WALL_SIGNS);
            } else {
                definition.addTags(BlockTags.SIGNS);
            }

            definition
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollission()
                    .strength(1.0F);
        }
    }
}
