/**
 * The builder interfaces returned by the fluent {@link org.betterx.wover.tabs.api.CreativeTabs} chain.
 *
 * <p>The builder progresses through stages, each represented by an interface in this package:
 * <ol>
 *   <li>{@link org.betterx.wover.tabs.api.interfaces.CreativeTabsBuilder} - create one or more tabs via
 *   {@code createTab}/{@code createBlockOnlyTab}/{@code createItemOnlyTab}</li>
 *   <li>{@link org.betterx.wover.tabs.api.interfaces.CreativeTabBuilder} - configure a single tab (icon, title,
 *   predicate) and add it back to the manager via {@code buildAndAdd()}</li>
 *   <li>{@link org.betterx.wover.tabs.api.interfaces.CreativeTabsBuilderWithTab} - once at least one tab exists,
 *   populate tabs with items via {@code process(...)}/{@code processRegistries()}</li>
 *   <li>{@link org.betterx.wover.tabs.api.interfaces.CreativeTabsBuilderWithItems} - once tabs are populated,
 *   register them with the game via {@code registerAllTabs()}</li>
 * </ol>
 *
 * <p>{@link org.betterx.wover.tabs.api.interfaces.CreativeTabPredicate} decides which items are accepted into a
 * given tab while items are being processed.
 */
package org.betterx.wover.tabs.api.interfaces;
