package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.client.trait.RenderLayerTrait;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class LadderBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new LadderBlockBuilder();
    private final List<BlockTrait<?, ?>> DEFAULT;

    private LadderBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_ladder"));
        DEFAULT = combine(
                new Trait(),
                ClientBlockTraits.RENDER_LAYER.with(RenderLayerTrait.Layer.CUTOUT),
                BlockTraits.CLIMBABLE.withDefault(),
                BlockTraits.LOOT_TABLE.dropSelf()
        );
    }

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
            definition.forceSolidOff()
                      .strength(0.4F)
                      .sound(SoundType.LADDER)
                      .noOcclusion()
                      .pushReaction(PushReaction.DESTROY);
        }
    }
}
