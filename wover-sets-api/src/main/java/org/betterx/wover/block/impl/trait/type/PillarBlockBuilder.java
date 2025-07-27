package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class PillarBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new PillarBlockBuilder();

    private PillarBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_pillar"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(
                BlockTraits.LOOT_TABLE.dropSelf()
        );
    }
}
