package org.betterx.wover.datagen.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.ItemTagDataProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;


/**
 * Creates item tags for all blocks or items that implement {@link ItemTagDataProvider}
 * and are registered in the namespace of this mod.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoItemTagProvider extends WoverTagProvider.ForItems {
    public AutoItemTagProvider(
            ModCore modCore
    ) {
        super(modCore);
    }

    @Override
    protected void prepareTags(ItemTagBootstrapContext provider) {
        BuiltInRegistries.BLOCK
                .entrySet()
                .stream()
                .filter(entry -> {
                    assert modIDs != null;
                    return modIDs.contains(entry.getKey().location().getNamespace());
                })
                .forEach(entry -> {
                    addBlockItemTags(provider, entry.getKey(), entry.getValue());
                });

        BuiltInRegistries.ITEM
                .entrySet()
                .stream()
                .filter(entry -> {
                    assert modIDs != null;
                    return modIDs.contains(entry.getKey().location().getNamespace());
                })
                .forEach(entry -> {
                    addItemTags(provider, entry.getKey(), entry.getValue());
                });
    }

    private void addBlockItemTags(ItemTagBootstrapContext provider, ResourceKey<Block> blockKey, Block block) {
        if (block instanceof ItemTagDataProvider tagProvider) {
            tagProvider.addItemTags(provider);
        }
    }

    private void addItemTags(ItemTagBootstrapContext provider, ResourceKey<Item> itemKey, Item item) {
        if (item instanceof ItemTagDataProvider tagProvider) {
            tagProvider.addItemTags(provider);
        }
    }
}
