package de.ambertation.wover.block.impl.client.trait;


import de.ambertation.wover.client.api.ClientTraitBootstrap;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.client.model.ClientItemModelRegistry;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;
import de.ambertation.wover.item.api.model.ItemModelBinding;
import de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.api.trait.RuntimeItemTrait;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiPredicate;

public class ItemModelTraitBuilder extends AbstractItemTraitBuilder<Item, ItemModelTrait> implements ItemModelTrait.Builder {
    public static final ItemModelTrait.Builder BUILDER = new ItemModelTraitBuilder();

    private ItemModelTraitBuilder() {
        // Shares the identity with ItemModelBinding, so the datagen walk collects both bindings and lambdas.
        super(ItemModelBinding.MODEL_TRAIT_KEY);
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
        ClientTraitBootstrap.ensureRegistered();

        ItemRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    ItemTrait.runtimeTraits(e.getValue())
                             .filter(trait -> trait.is(ItemModelBinding.MODEL_TRAIT_KEY))
                             .forEach(trait -> apply(e.getKey(), e.getValue(), generator, trait));
                });
    }

    @Environment(EnvType.CLIENT)
    private static void apply(
            ResourceKey<Item> key,
            Item item,
            ItemModelGenerators generator,
            RuntimeItemTrait<?, ?> trait
    ) {
        try {
            if (trait instanceof ItemModelBinding binding) {
                ClientItemModelRegistry.apply(binding, key, item, generator);
            } else if (trait instanceof ItemModelTrait modelTrait) {
                modelTrait.modelFactory().provideItemModels(key, item, generator);
            }
        } catch (Exception ex) {
            LibWoverRecipe.C.LOG.error("Failed to build model for item: " + key, ex);
        }
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
