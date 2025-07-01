package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

public interface GenericBlockTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    interface BuilderWithDefaults extends TraitBuilder.WithDefaults<Block, BlockTrait.VoidRuntime<Block>> {
    }

    interface BuilderWithDefault extends TraitBuilder.WithDefault<Block, BlockTrait.VoidRuntime<Block>> {
    }

    interface Builder extends TraitBuilder<Block, BlockTrait.VoidRuntime<Block>> {
    }
}
