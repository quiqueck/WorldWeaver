package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockTraitBuilder<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    interface WithDefault<B extends Block, R extends RuntimeBlockTrait<B, R>> extends BlockTraitBuilder<B, R> {
        @Nullable BlockTrait<?, ?> withDefault();
    }

    interface WithDefaults<B extends Block, R extends RuntimeBlockTrait<B, R>> extends BlockTraitBuilder<B, R> {
        @Nullable List<BlockTrait<?, ?>> withDefault();
    }

    @NotNull BlockTraitKey key();
    List<R> getRuntimeTraits(B block);
}
