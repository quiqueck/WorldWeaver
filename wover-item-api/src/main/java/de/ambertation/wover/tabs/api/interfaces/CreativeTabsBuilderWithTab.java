package de.ambertation.wover.tabs.api.interfaces;

import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.tabs.impl.CreativeTabBuilderImpl;

import net.minecraft.world.item.Item;

import java.util.stream.Stream;

public interface CreativeTabsBuilderWithTab extends CreativeTabsBuilder {
    /**
     * Process all items and block items from the {@link ItemRegistry} and BlockRegistry
     * for the current mod. (as defined by the ModCore that was used when creating the manager)
     * <p>
     * see {@link #process(Stream)} for details what processing means
     *
     * @return the same instance of the manager
     */
    CreativeTabsBuilderWithItems processRegistries();

    /**
     * Process a stream of items and add them to the appropriate tabs.
     * <p>
     * Processing items will test the predicate
     * (see {@link CreativeTabBuilderImpl#setPredicate(CreativeTabPredicate)})
     * for every item for each tab and add the item to
     * the first tab that has a valid predicate.
     *
     * @param items the stream of items to process
     * @return the same instance of the manager
     */
    CreativeTabsBuilderWithItems process(Stream<Item> items);
}
