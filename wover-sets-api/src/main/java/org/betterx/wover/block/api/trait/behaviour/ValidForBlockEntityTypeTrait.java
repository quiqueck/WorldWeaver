package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.impl.trait.BlockEntityTypeAccessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

public interface ValidForBlockEntityTypeTrait extends BlockTrait<Block, ValidForBlockEntityTypeTrait> {
    interface Builder extends BlockTraitBuilder<Block, ValidForBlockEntityTypeTrait> {
        ValidForBlockEntityTypeTrait with(@NotNull BlockEntityType<?> blockEntityType);
    }

    @NotNull
    BlockEntityType<?> getBlockEntityType();

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
