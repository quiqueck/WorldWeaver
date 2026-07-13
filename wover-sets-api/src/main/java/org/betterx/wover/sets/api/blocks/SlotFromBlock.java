package org.betterx.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

/**
 * A {@link SlotFactory} that reuses an already-existing {@link Block} for a slot, instead of building a new one.
 * Useful for filling a slot with a block registered elsewhere (e.g. a vanilla block, or one shared between sets).
 */
public class SlotFromBlock implements SlotFactory {
    private final SlotType slot;
    private final Block block;

    /**
     * @param slot  the slot this factory fills
     * @param block the block to use for that slot
     */
    public SlotFromBlock(SlotType slot, Block block) {
        this.slot = slot;
        this.block = block;
    }

    @Override
    public SlotType slot() {
        return this.slot;
    }

    /**
     * @return the block this factory reuses
     */
    public Block block() {
        return this.block;
    }

    @Override
    public void createBlockDefinition(BlockSet<?> set, BiConsumer<SlotType, Block> blockDefinitionConsumer) {
        blockDefinitionConsumer.accept(slot, block);
    }
}
