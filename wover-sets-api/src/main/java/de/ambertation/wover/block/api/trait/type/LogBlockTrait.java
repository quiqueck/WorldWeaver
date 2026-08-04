package de.ambertation.wover.block.api.trait.type;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GenericBlockTrait} marking a block as the "log"/"stripped log" member of a wood set (see
 * {@code de.ambertation.wover.sets.api.blocks.types.Log}). This trait itself carries no data; the strippable
 * behavior it configures is provided by combining it with a {@link StripableBlockTrait}, see
 * {@link Builder}.
 */
public interface LogBlockTrait extends GenericBlockTrait {
    /**
     * Builds {@link LogBlockTrait} instances, bundled with a matching {@link StripableBlockTrait} whenever a
     * stripped block state is supplied.
     */
    interface Builder extends GenericBlockTrait.BuilderWithDefaults {
        /**
         * @param strippedBlockState the block whose default state this log strips to, or {@code null} for a
         *                           non-strippable log
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(Block strippedBlockState);

        /**
         * @param strippedBlockState the state this log strips to, or {@code null} for a non-strippable log
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(BlockState strippedBlockState);

        /**
         * @param strippedBlockState computes the state this log strips to, or {@code null} for a non-strippable
         *                           log
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<BlockTrait<?, ?>> with(StripableBlockTrait.BlockStateFactory strippedBlockState);
    }
}
