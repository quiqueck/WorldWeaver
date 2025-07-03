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
    private final List<BlockTrait<?, ?>> DEFAULT;

    private BarrelBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_barrel"));
        DEFAULT = combine(
                new Trait(),
                BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.BARREL),
                BlockTraits.LOOT_TABLE.dropNamedEntity()
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
            definition.noOcclusion();

            if (ModCore.isDatagen()) {
                definition.addTags(CommonBlockTags.BARREL);
                definition.addItemTags(CommonItemTags.BARREL);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(CommonBlockTags.WOODEN_BARREL);
                    definition.addItemTags(CommonItemTags.WOODEN_BARREL);
                }
            }
        }
    }
}