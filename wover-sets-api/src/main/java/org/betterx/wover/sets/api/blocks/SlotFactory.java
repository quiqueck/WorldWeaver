package org.betterx.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public interface SlotFactory {
    SlotType slot();
    void createBlockDefinition(BlockSet<?> set, BiConsumer<SlotType, Block> blockDefinitionConsumer);
}
