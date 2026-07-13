package org.betterx.wover.tabs.api.interfaces;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * Decides whether a given {@link Item} belongs in a creative tab.
 * Set on a tab via {@link CreativeTabBuilder#setPredicate(CreativeTabPredicate)} and evaluated for every item
 * when tabs are populated (see {@link CreativeTabsBuilderWithTab#process(java.util.stream.Stream)}).
 */
public interface CreativeTabPredicate {
    /**
     * Matches items that are {@link BlockItem}s.
     */
    CreativeTabPredicate BLOCKS = item -> item instanceof BlockItem;
    /**
     * Matches items that are not {@link BlockItem}s.
     */
    CreativeTabPredicate ITEMS = item -> !(item instanceof BlockItem);

    /**
     * Checks whether the given item belongs in the tab this predicate is attached to.
     *
     * @param item The item to test
     * @return {@code true} if the item should be added to the tab, {@code false} otherwise
     */
    boolean contains(Item item);
}
