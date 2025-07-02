package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class ComposterBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefault {
    public static final GenericBlockTrait.BuilderWithDefault BUILDER = new ComposterBlockBuilder();

    private ComposterBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_composter"));
    }

    public @Nullable BlockTrait<?, ?> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return new ComposterBlockBuilder.Trait();
    }

    public class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(CommonBlockTags.COMPOSTER);
            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(CommonBlockTags.WOODEN_COMPOSTER);
            }
        }
    }
}
