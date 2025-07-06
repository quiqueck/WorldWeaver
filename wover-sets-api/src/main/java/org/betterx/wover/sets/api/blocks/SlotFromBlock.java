package org.betterx.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

public class SlotFromBlock implements SlotFactory {
    private final SlotType slot;
    private final Block block;

    public SlotFromBlock(SlotType slot, Block block) {
        this.slot = slot;
        this.block = block;
    }

    @Override
    public SlotType slot() {
        return this.slot;
    }

    public Block block() {
        return this.block;
    }

    @Override
    public void createBlockDefinition(BlockSet<?> set, BiConsumer<SlotType, Block> blockDefinitionConsumer) {
        blockDefinitionConsumer.accept(slot, block);
    }
}
