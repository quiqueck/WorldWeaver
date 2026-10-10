package de.ambertation.wover.block.api.trait;

/**
 * Implemented by a {@link RuntimeBlockTrait} whose block should be compostable.
 * <p>
 * This lets consumers that must not depend on the concrete trait implementation read the composting rate
 * generically: the {@code block_registrations.txt} datagen oracle reports the {@code compostable=} column
 * from it, and {@code BlockDefinition} turns it into the block item's {@code minecraft:compostable} component
 * at registration.
 * <p>
 * The chance is a legacy shape. 26.3 replaced the arbitrary {@code float} that
 * {@code ComposterBlock#addItem} used to roll with a reference into the {@code number_provider} registry, so
 * the rates an item can actually have are the finite set in {@link CompostTier}. Implementations keep
 * declaring a chance and it is snapped, which is why {@link #compostTier()} - not {@link #compostChance()} -
 * is what the game ends up using.
 */
public interface CompostableTrait {
    /**
     * The composting chance requested for this block's item. Snapped to the nearest {@link CompostTier}
     * before it reaches the game.
     *
     * @return the chance in {@code (0,1]}
     */
    float compostChance();

    /**
     * The tier this trait's item actually composts at.
     *
     * @return the tier nearest to {@link #compostChance()}
     */
    default CompostTier compostTier() {
        return CompostTier.nearest(compostChance());
    }
}
