package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.impl.trait.BlockEntityTypeAccessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link BlockTrait} that marks a block as a valid placement target for a given {@link BlockEntityType}, e.g.
 * a custom block that should be able to host a vanilla {@code BlockEntityType} normally restricted to specific
 * blocks (like {@code BlockEntityType#HANGING_SIGN}).
 */
public interface ValidForBlockEntityTypeTrait extends BlockTrait<Block, ValidForBlockEntityTypeTrait> {
    /**
     * Builds {@link ValidForBlockEntityTypeTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, ValidForBlockEntityTypeTrait> {
        /**
         * Creates a trait that marks the block as valid for {@code blockEntityType}.
         *
         * @param blockEntityType the block entity type to allow this block to host
         * @return the new trait
         */
        ValidForBlockEntityTypeTrait with(@NotNull BlockEntityType<?> blockEntityType);
    }

    /**
     * @return the block entity type this trait marks the block as valid for
     */
    @NotNull
    BlockEntityType<?> getBlockEntityType();

    /**
     * Directly registers {@code block} as a valid placement target for {@code blockEntityType}, without going
     * through the trait/definition machinery. Requires {@code blockEntityType} to implement the internal
     * {@code BlockEntityTypeAccessor} mixin interface (true for every vanilla and WoVer-created block entity
     * type).
     *
     * @param block           the block to allow
     * @param blockEntityType the block entity type to extend
     * @throws IllegalArgumentException if {@code blockEntityType} does not implement the accessor interface
     */
    static void makeValid(Block block, BlockEntityType<?> blockEntityType) {
        if (blockEntityType instanceof BlockEntityTypeAccessor accessor) {
            var set = accessor.wover_getValidSet();
            if (set == null) {
                set = new HashSet<>();
                accessor.wover_setValidSet(set);
            }
            set.add(block);
        } else {
            throw new IllegalArgumentException(
                    "BlockEntityType must implement ValidForBlockEntityTypeAccessor to use this method"
            );
        }

    }
}
