package de.ambertation.wover.tabs.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabPredicate;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilder;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilderWithItems;
import de.ambertation.wover.tabs.api.interfaces.CreativeTabsBuilderWithTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class CreativeTabManagerImpl implements CreativeTabsBuilder, CreativeTabsBuilderWithTab, CreativeTabsBuilderWithItems {
    public final ModCore C;
    protected final List<SimpleCreativeTabImpl> tabs = new LinkedList<>();


    public CreativeTabManagerImpl(ModCore modCore) {
        this.C = modCore;
    }

    public CreativeTabBuilderImpl createTab(String name) {
        return new CreativeTabBuilderImpl(this, name);
    }

    public CreativeTabBuilderImpl createBlockOnlyTab(ItemLike icon) {
        return new CreativeTabBuilderImpl(this, "blocks").setIcon(icon).setPredicate(CreativeTabPredicate.BLOCKS);
    }

    public CreativeTabBuilderImpl createItemOnlyTab(ItemLike icon) {
        return new CreativeTabBuilderImpl(this, "items").setIcon(icon).setPredicate(CreativeTabPredicate.ITEMS);
    }


    public CreativeTabsBuilderWithItems processRegistries() {
        process(ItemRegistry.forMod(C).allItems());
        return this;
    }

    public CreativeTabsBuilderWithItems process(Stream<Item> items) {
        items.forEach(item -> {
            for (SimpleCreativeTabImpl tab : tabs) {
                if (tab.predicate.contains(item)) {
                    tab.addItem(item);
                    break;
                }
            }
        });

        return this;
    }

    public void registerAllTabs() {
        for (SimpleCreativeTabImpl tab : tabs) {
            var tabItem = FabricItemGroup
                    .builder()
                    .icon(() -> new ItemStack(tab.icon))
                    .title(tab.title)
                    .displayItems((displayParameters, output) -> {
                        output.acceptAll(tab.items
                                .stream()
                                .map(i -> ItemStackHelper.callItemStackSetupIfPossible(new ItemStack(i), displayParameters.holders()))
                                .toList()
                        );
                    }).build();

            Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    tab.key,
                    tabItem
            );
        }
    }
}

