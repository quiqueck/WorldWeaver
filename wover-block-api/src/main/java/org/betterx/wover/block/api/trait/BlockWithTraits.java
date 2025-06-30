package org.betterx.wover.block.api.trait;


import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface BlockWithTraits<B extends Block> {
    void wover_setTraits(@Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> traits);

    @Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> wover_traits();
}
