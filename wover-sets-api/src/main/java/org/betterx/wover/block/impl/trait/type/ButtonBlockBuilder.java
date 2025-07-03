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

public class ButtonBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefault {
    public static final GenericBlockTrait.BuilderWithDefault BUILDER = new ButtonBlockBuilder();
    private final Trait DEFAULT;

    private ButtonBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_button"));
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
                definition.addTags(BlockTags.BUTTONS);
                definition.addItemTags(ItemTags.BUTTONS);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.strength(0.5F, 0.5F);
                    definition.addTags(BlockTags.WOODEN_BUTTONS);
                    definition.addItemTags(ItemTags.WOODEN_BUTTONS);
                }

                if (definition.hasTrait(BlockTraits.STONE_BLOCK)) {
                    definition.addTags(BlockTags.STONE_BUTTONS);
                    definition.addItemTags(ItemTags.STONE_BUTTONS);
                }
            }
        }
    }
}
