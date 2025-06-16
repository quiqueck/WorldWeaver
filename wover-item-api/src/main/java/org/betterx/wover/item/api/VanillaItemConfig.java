package org.betterx.wover.item.api;

import net.minecraft.world.item.Item;

public final class VanillaItemConfig extends ItemConfig<Item, VanillaItemConfig> {
    public static final ItemFactory<Item, VanillaItemConfig> DEFAULT_FACTORY = config -> new Item(
            config.properties);

    VanillaItemConfig(ItemRegistry registry, String name) {
        super(registry, name, DEFAULT_FACTORY);
    }

    @Override
    protected void beforeBuild() {
        
    }
}
