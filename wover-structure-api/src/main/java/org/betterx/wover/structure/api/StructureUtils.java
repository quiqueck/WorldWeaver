package org.betterx.wover.structure.api;

import org.betterx.wover.structure.impl.StructureManagerImpl;

import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Utility class for Structures
 */
public class StructureUtils {
    private StructureUtils() {
    }

    /**
     * runs the {@code PieceGeneratorSupplier.Context::validBiome} from the given context at
     * height=5 in the middle of the chunk.
     *
     * @param context The context to test with.
     * @return true, if this feature can spawn in the current biome
     */
    public static boolean isValidBiome(Structure.GenerationContext context) {
        return StructureManagerImpl.isValidBiome(context);
    }

    /**
     * runs the {@code PieceGeneratorSupplier.Context::validBiome} from the given context at the
     * given height in the middle of the chunk.
     *
     * @param context The context to test with.
     * @param yPos    The Height to test for
     * @return true, if this feature can spawn in the current biome
     */
    public static boolean isValidBiome(Structure.GenerationContext context, int yPos) {
        return StructureManagerImpl.isValidBiome(context, yPos);
    }
}
