package org.betterx.wover.tag.impl;

import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ItemTagRegistryImpl extends TagRegistryImpl.WithRegistry<Item, ItemTagBootstrapContext> implements TagRegistry<Item, ItemTagBootstrapContext> {
    ItemTagRegistryImpl() {
        super(BuiltInRegistries.ITEM);
    }

    @Override
    public ItemTagBootstrapContext createBootstrapContext(boolean initAll) {
        return ItemTagBootstrapContextImpl.create(this, initAll);
    }
}
