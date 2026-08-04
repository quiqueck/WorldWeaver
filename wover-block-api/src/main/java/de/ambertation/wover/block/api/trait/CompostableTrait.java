package de.ambertation.wover.block.api.trait;

/**
 * Implemented by a {@link RuntimeBlockTrait} whose block should be compostable with a fixed chance that is
 * resolved <em>at runtime</em> - straight off the block via its attached runtime trait - rather than through
 * vanilla's static {@code ComposterBlock.COMPOSTABLES} map.
 * <p>
 * This lets consumers that must not depend on the concrete trait implementation read the composting chance
 * generically. In particular the {@code block_registrations.txt} datagen oracle reads it to report the
 * {@code compostable=} column for blocks whose chance never reaches the vanilla map, and a composter mixin
 * reads it to accept those blocks when a composter is filled.
 */
public interface CompostableTrait {
    /**
     * The composting chance used when this block's item is placed into a composter.
     *
     * @return the chance in {@code (0,1]}
     */
    float compostChance();
}
