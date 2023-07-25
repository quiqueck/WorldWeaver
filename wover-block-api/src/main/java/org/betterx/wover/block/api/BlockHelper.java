package org.betterx.wover.block.api;

import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class BlockHelper {
    public static boolean isFluid(BlockState state) {
        return state.liquid();
    }

    public static boolean isFreeOrFluid(BlockState state) {
        return state.isAir() || isFluid(state);
    }

    public static boolean isTerrain(BlockState state) {
        return state.is(CommonBlockTags.TERRAIN);
    }

    public static boolean findOnSurroundingSurface(
            LevelAccessor level,
            BlockPos.MutableBlockPos startPos,
            Direction dir,
            int length,
            Predicate<BlockState> surface
    ) {
        for (int len = 0; len < length; len++) {
            if (surface.test(level.getBlockState(startPos))) {
                if (len == 0) { //we started inside of the surface
                    for (int lenUp = 0; lenUp < length; lenUp++) {
                        startPos.move(dir, -1);
                        if (!surface.test(level.getBlockState(startPos))) {
                            return true;
                        }
                    }
                    return false;
                }
                startPos.move(dir, -1);
                return true;
            }

            startPos.move(dir, 1);
        }
        return false;
    }
}
