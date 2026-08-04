package de.ambertation.wover.block.impl.trait;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;

import net.minecraft.world.level.block.Block;

public abstract class BlockTraitImpl<B extends Block, R extends RuntimeBlockTrait<B, R>> implements BlockTrait<B, R> {
    protected BlockTraitImpl() {
    }

    @Override
    public R forRuntime() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof RuntimeBlockTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof BlockTraitKey key) {
            return this.is(key);
        }
        if (obj instanceof BlockTraitBuilder<?, ?> builder) {
            return this.is(builder);
        }

        return super.equals(obj);
    }

    @Override
    public void configure(BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition) {
        // Default implementation does nothing
    }

    @Override
    public void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition
    ) {
        // Default implementation does nothing
    }

    public abstract static class Generic extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {

    }
}
