package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitBuilder;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockTrait} that marks a block as strippable (e.g. logs or bark), so that an axe interaction turns it
 * into a matching stripped {@link BlockState}. Also exposes the stripped state at runtime via
 * {@link RuntimeBlockTrait}.
 */
public interface StripableBlockTrait extends BlockTrait<Block, StripableBlockTrait>, RuntimeBlockTrait<Block, StripableBlockTrait> {
    /**
     * Computes the stripped {@link BlockState} for a given original state.
     */
    interface BlockStateFactory {
        /**
         * @param oldState the state being stripped, or {@code null} if not yet known
         * @return the resulting stripped state
         */
        @NotNull BlockState create(@Nullable BlockState oldState);
    }

    /**
     * Builds {@link StripableBlockTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, StripableBlockTrait> {
        /**
         * Creates a trait that always strips to the default state of {@code strippedBlock}.
         *
         * @param strippedBlock the block to strip to, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable Block strippedBlock);

        /**
         * Creates a trait that always strips to a fixed {@link BlockState}.
         *
         * @param strippedBlock the state to strip to, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock);

        /**
         * Creates a trait that computes the stripped state from the original one via a factory, e.g. to preserve
         * shared properties like {@link net.minecraft.world.level.block.RotatedPillarBlock#AXIS} (see
         * {@link #copyRotatedPillarBlockState}).
         *
         * @param strippedBlock computes the stripped state, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable BlockStateFactory strippedBlock);

        /**
         * Resolves the stripped state for a given block state, without needing a built trait instance.
         *
         * @param blockState the state to strip
         * @return the resulting stripped state
         */
        @NotNull BlockState getStrippedBlockState(@Nullable BlockState blockState);
    }

    /**
     * @param oldState the state being stripped
     * @return the resulting stripped state
     */
    @NotNull BlockState strippedBlock(@Nullable BlockState oldState);

    /**
     * Wraps a {@link BlockStateFactory} so the resulting state also copies the
     * {@link net.minecraft.world.level.block.RotatedPillarBlock#AXIS} property from the original state, if
     * present. Used by log/bark-style blocks that keep their orientation when stripped.
     *
     * @param factory the factory to wrap
     * @return the wrapped factory
     */
    static @NotNull BlockStateFactory copyRotatedPillarBlockState(@NotNull BlockStateFactory factory) {
        return (oldState) -> {
            var newState = factory.create(oldState);
            if (oldState != null && oldState.hasProperty(RotatedPillarBlock.AXIS)) {
                return newState.setValue(
                        RotatedPillarBlock.AXIS,
                        oldState.getValue(RotatedPillarBlock.AXIS)
                );
            }
            return newState;
        };
    }
}
