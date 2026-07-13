/**
 * A fluent API for creating and registering custom creative-mode inventory tabs.
 *
 * <p>{@link org.betterx.wover.tabs.api.CreativeTabs#start(org.betterx.wover.core.api.ModCore)} is the entry point:
 * it returns a {@link org.betterx.wover.tabs.api.interfaces.CreativeTabsBuilder} used to create one or more tabs,
 * optionally populate them by scanning a mod's registered items, and finally register them with Minecraft's
 * creative mode tab registry.
 *
 * <h2>Usage Example</h2>
 * <pre class="java">
 * CreativeTabs.start(MyMod.C)
 *             .createItemOnlyTab(Items.WOODEN_AXE).buildAndAdd()
 *             .processRegistries()
 *             .registerAllTabs();
 * </pre>
 *
 * @see org.betterx.wover.tabs.api.CreativeTabs
 * @see org.betterx.wover.tabs.api.interfaces
 */
package org.betterx.wover.tabs.api;
