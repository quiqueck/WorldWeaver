package de.ambertation.wover.loot.api;

/**
 * Adds content to a loot table that already exists.
 * <p>
 * Register one through {@link LootTableAppenders}. It is called once per {@code /reload} for every table it
 * was registered for, with a fresh {@link MutableLootTable} each time, so an implementation must be a pure
 * function of the view it is given: build the pools and entries inside the method, never cache them, and
 * never keep the view.
 */
@FunctionalInterface
public interface LootTableAppender {
    /**
     * Adds this appender's content to the given table.
     *
     * @param table the table to extend; only valid for the duration of this call
     */
    void appendTo(MutableLootTable table);
}
