package de.ambertation.wover.tabs.impl;

import de.ambertation.wover.tabs.api.interfaces.CreativeTabPredicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.LinkedList;
import java.util.List;

public class SimpleCreativeTabImpl {
    public final Identifier id;
    public final ItemLike icon;
    public final Component title;
    public final CreativeTabPredicate predicate;

    public final ResourceKey<CreativeModeTab> key;

    SimpleCreativeTabImpl(Identifier id, ItemLike icon, Component title, CreativeTabPredicate predicate) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.predicate = predicate;
        this.key = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                id
        );
        this.items = new LinkedList<>();
    }

    protected final List<Item> items;

    void addItem(Item item) {
        items.add(item);
    }
}
