package org.betterx.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

/**
 * Builds and registers the block(s) for a single {@link SlotType} within a {@link BlockSet}.
 * <p>
 * A {@link BlockSet} subclass overrides {@link BlockSet#createDefaultDefinitions()} to return a {@link SlotMap}
 * of {@code SlotFactory} instances, one per role the set should fill (e.g. planks, slab, stairs). The concrete
 * implementations used in practice are {@link SlotFromBlock} (wraps an already-existing {@link Block}) and
 * {@link SlotFromDefinition} (builds a new block via {@link org.betterx.wover.block.api.BlockDefinition}), plus
 * the ready-made block-type factories in {@code org.betterx.wover.sets.api.blocks.types}.
 */
public interface SlotFactory {
    /**
     * @return the slot this factory fills
     */
    SlotType slot();

    /**
     * Builds and registers this factory's block(s), reporting each one back through
     * {@code blockDefinitionConsumer} (a factory that produces more than one block, like a sign and its wall
     * variant, may call it more than once with different slots).
     *
     * @param set                      the block set this factory belongs to
     * @param blockDefinitionConsumer  receives each registered block together with its slot
     */
    void createBlockDefinition(BlockSet<?> set, BiConsumer<SlotType, Block> blockDefinitionConsumer);
}
