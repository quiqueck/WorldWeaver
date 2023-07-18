package org.betterx.wover.tag.impl;

import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

public class ItemTagBootstrapContextImpl extends TagBootstrapContextImpl<Item, ItemTagBootstrapContext> implements ItemTagBootstrapContext {
    ItemTagBootstrapContextImpl(@Nullable ItemTagRegistryImpl tagRegistry, boolean initAll) {
        super(tagRegistry, initAll);
    }

    @SafeVarargs
    public final void add(TagKey<Item> tagID, ItemLike... elements) {
        for (ItemLike element : elements) {
            add(tagID, false, element.asItem());
        }
    }

    @SafeVarargs
    public final void addOptional(TagKey<Item> tagID, ItemLike... elements) {
        for (ItemLike element : elements) {
            add(tagID, true, element.asItem());
        }
    }

    @SafeVarargs
    public final void add(ItemLike element, TagKey<Item>... tags) {
        super.add(element.asItem(), tags);
    }

    @SafeVarargs
    public final void addOptional(ItemLike element, TagKey<Item>... tags) {
        super.addOptional(element.asItem(), tags);
    }
}
