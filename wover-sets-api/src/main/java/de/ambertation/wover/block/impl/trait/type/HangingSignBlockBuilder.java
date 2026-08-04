package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class HangingSignBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new HangingSignBlockBuilder();
    private final List<BlockTrait<?, ?>> DEFAULT;

    private HangingSignBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_hanging_sign"));
        DEFAULT = combine(
                new Trait(),
                BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.HANGING_SIGN),
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
            if (ModCore.isDatagen()) {
                if ((Object) definition instanceof WallSignBlockDefinition wall) {
                    definition.addTags(BlockTags.WALL_HANGING_SIGNS);
                } else {
                    definition.addTags(BlockTags.CEILING_HANGING_SIGNS);
                }
            }

            definition.forceSolidOn()
                      .instrument(NoteBlockInstrument.BASS)
                      .noCollission()
                      .strength(1.0F);
        }
    }
}
