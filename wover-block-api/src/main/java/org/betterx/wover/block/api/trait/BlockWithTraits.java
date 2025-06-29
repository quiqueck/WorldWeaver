package org.betterx.wover.block.api.trait;


import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface BlockWithTraits<B extends Block> {
    void wover_setTraits(@Nullable List<RuntimeBlockTrait<B, ?>> traits);

    @Nullable Collection<RuntimeBlockTrait<B, ?>> wover_traits();
}
