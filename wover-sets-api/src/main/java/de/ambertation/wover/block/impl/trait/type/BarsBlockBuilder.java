package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BarsBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new BarsBlockBuilder();
    private final List<BlockTrait<?, ?>> DEFAULT;

    private BarsBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_bars"));
        DEFAULT = combine(
                new BarsBlockBuilder.Trait(),
                BlockRenderTraits.RENDER_LAYER.cutout(),
                BlockTraits.LOOT_TABLE.dropSelfNoExplosion()
        );
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        return DEFAULT;
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.noOcclusion();
        }
    }
}
