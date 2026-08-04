package de.ambertation.wover.tabs.api.interfaces;

import de.ambertation.wover.tabs.impl.CreativeTabBuilderImpl;

import net.minecraft.world.level.ItemLike;

/**
 * Entry point for creating individual creative-mode tabs for a mod. Obtained via
 * {@link de.ambertation.wover.tabs.api.CreativeTabs#start(de.ambertation.wover.core.api.ModCore)}.
 */
public interface CreativeTabsBuilder {
    /**
     * Starts building a new, freely configurable tab with the given name.
     * The tab's translation key defaults to {@code itemGroup.<namespace>.<name>} and must have an icon set via
     * {@link CreativeTabBuilder#setIcon(ItemLike)} before it can be added.
     *
     * @param name The name identifier for the tab, used to derive its id and default translation key
     * @return A new tab builder for further configuration
     */
    CreativeTabBuilderImpl createTab(String name);

    /**
     * Starts building a tab named {@code "blocks"} that only accepts {@link net.minecraft.world.item.BlockItem}s
     * (via {@link CreativeTabPredicate#BLOCKS}), with the given icon already set.
     *
     * @param icon The item used as the tab's icon
     * @return A new tab builder for further configuration
     */
    CreativeTabBuilderImpl createBlockOnlyTab(ItemLike icon);

    /**
     * Starts building a tab named {@code "items"} that only accepts non-block items
     * (via {@link CreativeTabPredicate#ITEMS}), with the given icon already set.
     *
     * @param icon The item used as the tab's icon
     * @return A new tab builder for further configuration
     */
    CreativeTabBuilderImpl createItemOnlyTab(ItemLike icon);
}
