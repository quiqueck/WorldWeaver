package de.ambertation.wover.tabs.api;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilder;
import de.ambertation.wover.tabs.impl.CreativeTabManagerImpl;

/**
 * Entry point for building and registering custom creative-mode inventory tabs for a mod.
 *
 * <p>Usage example:</p>
 * <pre class="java">
 * CreativeTabs.start(MyMod.C)
 *             .createItemOnlyTab(Items.WOODEN_AXE).buildAndAdd()
 *             .processRegistries()
 *             .registerAllTabs();
 * </pre>
 *
 * @see de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilder
 */
public class CreativeTabs {
    /**
     * Starts building creative tabs for the given mod.
     *
     * @param modCore The mod core the tabs will belong to
     * @return A new builder used to create and configure tabs for this mod
     */
    public static CreativeTabsBuilder start(ModCore modCore) {
        return new CreativeTabManagerImpl(modCore);
    }
}

