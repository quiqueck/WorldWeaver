package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

public interface GenericBlockTrait extends BlockTrait<Block, GenericBlockTrait> {
    interface BuilderWithDefaults extends BlockTraitBuilder.WithDefaults<Block, GenericBlockTrait> {
    }

    interface BuilderWithDefault extends BlockTraitBuilder.WithDefault<Block, GenericBlockTrait> {
    }

    interface Builder extends BlockTraitBuilder<Block, GenericBlockTrait> {
    }
}
