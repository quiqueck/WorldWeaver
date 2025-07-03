package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class FenceBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefault {
    public static final GenericBlockTrait.BuilderWithDefault BUILDER = new FenceBlockBuilder();

    private final Trait DEFAULT;

    private FenceBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_fence"));
        DEFAULT = new Trait();
    }

    public @Nullable BlockTrait<?, ?> withDefault() {
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
                definition.addTags(BlockTags.FENCES);
                definition.addItemTags(ItemTags.FENCES);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(BlockTags.WOODEN_FENCES);
                    definition.addItemTags(ItemTags.WOODEN_FENCES);
                }
            }
        }
    }
}
