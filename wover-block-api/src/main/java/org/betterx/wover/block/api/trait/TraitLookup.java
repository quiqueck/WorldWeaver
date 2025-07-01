package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.impl.trait.BlockTraitImpl;

public interface TraitLookup {
    boolean hasTrait(BlockTraitImpl<?, ?> trait);
    boolean hasTrait(BlockTraitKey traitKey);

    boolean hasTrait(BlockTraitBuilder<?, ?> traitBuilder);
}
