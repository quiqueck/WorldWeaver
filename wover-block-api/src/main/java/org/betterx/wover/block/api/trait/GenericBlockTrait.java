package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

/**
 * A {@link BlockTrait} that applies to any {@link Block} rather than to a specific block subclass. Most
 * behaviour-style traits (e.g. {@link org.betterx.wover.block.api.trait.behaviour.FlammableBlockTrait}) use
 * this as their base, since they don't need any specific block subclass to function.
 */
public interface GenericBlockTrait extends BlockTrait<Block, GenericBlockTrait> {
    /**
     * A {@link BlockTraitBuilder.WithDefaults} for {@link GenericBlockTrait}s.
     */
    interface BuilderWithDefaults extends BlockTraitBuilder.WithDefaults<Block, GenericBlockTrait> {
    }

    /**
     * A {@link BlockTraitBuilder.WithDefault} for {@link GenericBlockTrait}s.
     */
    interface BuilderWithDefault extends BlockTraitBuilder.WithDefault<Block, GenericBlockTrait> {
    }

    /**
     * A {@link BlockTraitBuilder} for {@link GenericBlockTrait}s.
     */
    interface Builder extends BlockTraitBuilder<Block, GenericBlockTrait> {
    }
}
