package org.betterx.wover.block.api.trait;


import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface BlockWithTraits<B extends Block> {
    void wover_setTraits(@Nullable List<BlockTrait.RuntimeTrait<B, ?>> traits);

    @Nullable Collection<BlockTrait.RuntimeTrait<B, ?>> wover_traits();
}
