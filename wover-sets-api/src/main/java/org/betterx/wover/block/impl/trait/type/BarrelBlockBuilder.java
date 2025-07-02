package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BarrelBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new BarrelBlockBuilder();

    private BarrelBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_barrel"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.BARREL));
        return combine(new Trait(), BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.BARREL));
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(CommonBlockTags.BARREL);
            definition.addItemTags(CommonItemTags.BARREL);

            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(CommonBlockTags.WOODEN_BARREL);
                definition.addItemTags(CommonItemTags.WOODEN_BARREL);
            }
        }
    }
}