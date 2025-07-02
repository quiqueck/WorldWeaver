package org.betterx.wover.item.api.client.trait;

import org.betterx.wover.block.impl.client.trait.ItemModelTraitBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitBuilder;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;

public interface ItemModelTrait extends ItemTrait<Item, ItemModelTrait> {
    interface ModelFactory {
        void provideItemModels(ResourceKey<Item> key, Item block, ItemModelGenerators generator);
    }

    interface Builder extends ItemTraitBuilder<Item, ItemModelTrait> {
        ItemModelTrait with(ModelFactory factory);
    }

    ModelFactory modelFactory();

    static void bootstrapModels(
            ModCore modCore,
            ItemModelGenerators generator
    ) {
        ItemModelTraitBuilder.bootstrapModels(modCore, generator, (k, b) -> true);
    }

    static void bootstrapModels(
            ModCore modCore,
            ItemModelGenerators generator,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemModelTraitBuilder.bootstrapModels(modCore, generator, filter);
    }
}
