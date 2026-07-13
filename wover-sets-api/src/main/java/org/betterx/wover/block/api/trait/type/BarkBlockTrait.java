package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GenericBlockTrait} marking a block as the "bark"/"stripped bark" member of a wood set (see
 * {@code org.betterx.wover.sets.api.blocks.types.Bark}). This trait itself carries no data; the strippable
 * behavior it configures is provided by combining it with a {@link StripableBlockTrait}, see
 * {@link Builder}.
 */
public interface BarkBlockTrait extends GenericBlockTrait {
    /**
     * Builds {@link BarkBlockTrait} instances, bundled with a matching {@link StripableBlockTrait} whenever a
     * stripped block state is supplied.
     */
    interface Builder extends GenericBlockTrait.BuilderWithDefaults {
        /**
         * @param strippedBlockState the block whose default state bark strips to, or {@code null} for
         *                           non-strippable bark
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(@Nullable Block strippedBlockState);

        /**
         * @param strippedBlockState the state bark strips to, or {@code null} for non-strippable bark
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(@Nullable BlockState strippedBlockState);

        /**
         * @param strippedBlockState computes the state bark strips to, or {@code null} for non-strippable bark
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(@Nullable StripableBlockTrait.BlockStateFactory strippedBlockState);
    }
}
