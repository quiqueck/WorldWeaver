package de.ambertation.wover.tabs.api.interfaces;

import de.ambertation.wover.tabs.impl.CreativeTabBuilderImpl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

/**
 * Fluent builder for a single creative-mode inventory tab, created via
 * {@link CreativeTabsBuilder#createTab(String)}.
 */
public interface CreativeTabBuilder {
    /**
     * Sets the icon item displayed on the tab. Required before {@link #buildAndAdd()}.
     *
     * @param icon The item used as the tab's icon
     * @return This builder for method chaining
     */
    CreativeTabBuilderImpl setIcon(ItemLike icon);

    /**
     * Sets the predicate used to decide which items are added to this tab when items are processed
     * (see {@link CreativeTabsBuilderWithTab#process(java.util.stream.Stream)}).
     * Defaults to accepting every item if not set.
     *
     * @param predicate The predicate deciding whether an item belongs in this tab
     * @return This builder for method chaining
     */
    CreativeTabBuilderImpl setPredicate(CreativeTabPredicate predicate);

    /**
     * Sets the display title of the tab. Defaults to a translatable component derived from the tab's name.
     *
     * @param title The title component shown for this tab
     * @return This builder for method chaining
     */
    CreativeTabBuilderImpl setTitle(Component title);

    /**
     * Finalizes this tab and adds it to the parent {@link CreativeTabsBuilder}.
     *
     * @return The parent builder, for chaining further tab creation or processing
     * @throws IllegalStateException if no icon was set
     */
    CreativeTabsBuilderWithTab buildAndAdd();
}
