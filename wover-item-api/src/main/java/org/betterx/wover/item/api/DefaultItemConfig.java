package org.betterx.wover.item.api;

import net.minecraft.world.item.Item;

public final class DefaultItemConfig<I extends Item> extends ItemConfig<I, DefaultItemConfig<I>> {
    public interface ItemFactory<I extends Item> extends ItemConfig.ItemFactory<I, DefaultItemConfig<I>> {
    }

    DefaultItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, DefaultItemConfig<I>> itemFactory
    ) {
        super(registry, name, itemFactory);
    }

    @Override
    protected void beforeBuild() {
        
    }
}
