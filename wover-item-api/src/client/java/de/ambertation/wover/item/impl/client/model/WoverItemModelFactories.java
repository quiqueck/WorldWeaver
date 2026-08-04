package de.ambertation.wover.item.impl.client.model;

import de.ambertation.wover.client.api.WoverClientTraitEntrypoint;
import de.ambertation.wover.client.impl.ClientRenderTraitRegistry;
import de.ambertation.wover.item.api.client.model.ClientItemModelRegistry;
import de.ambertation.wover.item.api.model.ItemModelKeys;
import de.ambertation.wover.item.impl.client.render.ClientBoatRenderBootstrap;

import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Registers the built-in item model factories against their {@link ItemModelKeys}. The bodies are the client-only
 * item-model generator code that used to live in {@code ModelTraitLibrary.Impl} / {@code ItemModelTraitLibrary};
 * it is now keyed so common code can attach a data-only {@code ItemModelBinding}. Invoked through the
 * {@code wover.client.traits} entrypoint.
 */
@Environment(EnvType.CLIENT)
public class WoverItemModelFactories implements WoverClientTraitEntrypoint {
    @Override
    public void registerClientTraits() {
        ClientItemModelRegistry.register(ItemModelKeys.FLAT_ITEM, (key, item, generator, payload) -> {
            generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });

        ClientItemModelRegistry.register(ItemModelKeys.FLAT_ITEM_FROM, (key, item, generator, material) -> {
            final var modelLocation = ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(item),
                    TextureMapping.layer0(ModelLocationUtils.getModelLocation(material.get())),
                    generator.modelOutput
            );
            generator.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
        });

        ClientItemModelRegistry.register(ItemModelKeys.ELYTRA, (key, elytra, generator, payload) -> {
            ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(generator.createFlatItemModel(
                    elytra,
                    ModelTemplates.FLAT_ITEM
            ));
            ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(generator.createFlatItemModel(
                    elytra,
                    "_broken",
                    ModelTemplates.FLAT_ITEM
            ));
            generator.generateBooleanDispatch(elytra, new Broken(), unbaked2, unbaked);
        });

        ClientItemModelRegistry.register(ItemModelKeys.BOAT, (key, item, generator, payload) -> {
            generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });

        // Boat renderer/model-layer registration is applied (walking the item registry) once at client init.
        ClientRenderTraitRegistry.register(ClientBoatRenderBootstrap::applyBoatRenderers);
    }
}
