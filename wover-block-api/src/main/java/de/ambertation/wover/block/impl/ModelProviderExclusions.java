package de.ambertation.wover.block.impl;

import net.minecraft.world.level.block.Block;

import java.util.LinkedList;
import java.util.List;

/**
 * The vanilla Model provider will fail, if it does not include a model-definition for every
 * registered block.
 * You can exclude blocks from that check by calling {@link #excludeFromBlockModelValidation(Block)}
 */

public class ModelProviderExclusions {
    private static List<Block> wover_excludedBlocks = new LinkedList<>();

    public static void excludeFromBlockModelValidation(Block b) {
        wover_excludedBlocks.add(b);
    }

    public static boolean isExcluded(Block b) {
        return wover_excludedBlocks.contains(b);
    }
}
