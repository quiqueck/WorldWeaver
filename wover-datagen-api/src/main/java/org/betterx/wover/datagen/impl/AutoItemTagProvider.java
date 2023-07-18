package org.betterx.wover.datagen.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.ItemTagDataProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Creates item tags for all blocks or items that implement {@link ItemTagDataProvider}
 * and are registered in the namespace of this mod.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoItemTagProvider extends WoverTagProvider.ForItems {
    public AutoItemTagProvider(
            ModCore modCore,
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(List.of(modCore.namespace, modCore.modId), output, registriesFuture);
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
                    if (entry.getValue() instanceof ItemTagDataProvider tagProvider) {
                        tagProvider.addItemTags(provider);
                    }
                });

        BuiltInRegistries.ITEM
                .entrySet()
                .stream()
                .filter(entry -> {
                    assert modIDs != null;
                    return modIDs.contains(entry.getKey().location().getNamespace());
                })
                .forEach(entry -> {
                    if (entry.getValue() instanceof ItemTagDataProvider tagProvider) {
                        tagProvider.addItemTags(provider);
                    }
                });
    }
}
