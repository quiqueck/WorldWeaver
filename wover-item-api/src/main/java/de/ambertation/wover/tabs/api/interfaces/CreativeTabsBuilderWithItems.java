package de.ambertation.wover.tabs.api.interfaces;

/**
 * A {@link CreativeTabsBuilderWithTab} whose tabs have already been populated with items and are ready to be
 * registered with the game's creative mode tab registry.
 */
public interface CreativeTabsBuilderWithItems extends CreativeTabsBuilderWithTab {
    /**
     * Registers every tab created so far with Minecraft's creative mode tab registry, making them appear in the
     * creative inventory. This should be called once, after all tabs have been created and populated.
     */
    void registerAllTabs();
}
