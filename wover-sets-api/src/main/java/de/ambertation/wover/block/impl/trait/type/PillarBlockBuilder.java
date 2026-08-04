package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

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
