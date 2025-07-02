package org.betterx.wover.block.impl.client.trait;


import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.ItemTraitKey;
import org.betterx.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiPredicate;

public class ItemModelTraitBuilder extends AbstractItemTraitBuilder<Item, ItemModelTrait> implements ItemModelTrait.Builder {
    public static final ItemModelTrait.Builder BUILDER = new ItemModelTraitBuilder();

    private ItemModelTraitBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "model"));
    }

    @Override
    public ItemModelTrait with(ItemModelTrait.ModelFactory factory) {
        if (!ModCore.isDatagen() || !ModCore.isClient()) return null;
        return new Trait(factory);
    }


    @Environment(EnvType.CLIENT)
    public static void bootstrapModels(
            ModCore modCore,
            ItemModelGenerators generator,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = BUILDER.getRuntimeTraits(e.getValue());
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        try {
                            trait.modelFactory().provideItemModels(e.getKey(), e.getValue(), generator);
                        } catch (Exception ex) {
                            LibWoverRecipe.C.LOG.error("Failed to build model for block: " + e.getKey(), ex);
                        }
                    });
                });
    }

    @Environment(EnvType.CLIENT)
    class Trait extends ItemTraitImpl<Item, ItemModelTrait> implements ItemModelTrait {
        private final ModelFactory modelFactory;

        public Trait(ModelFactory modelFactory) {
            super();
            this.modelFactory = modelFactory;
        }


        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public ModelFactory modelFactory() {
            return this.modelFactory;
        }

        @Override
        public ItemModelTrait forRuntime() {
            return this;
        }
    }
}
