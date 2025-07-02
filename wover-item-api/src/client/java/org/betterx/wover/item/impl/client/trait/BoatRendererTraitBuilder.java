package org.betterx.wover.item.impl.client.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverItem;
import org.betterx.wover.item.api.BoatItemDefinition;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.client.trait.BoatRendererTrait;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.ItemTraitKey;
import org.betterx.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.item.BoatItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;


public class BoatRendererTraitBuilder extends AbstractItemTraitBuilder<BoatItem, BoatRendererTrait> implements BoatRendererTrait.Builder {
    public static final BoatRendererTrait.Builder BUILDER = new BoatRendererTraitBuilder();

    protected BoatRendererTraitBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverItem.C, "boat_model"));
    }

    @Override
    public BoatRendererTrait withDefault() {
        if (!ModCore.isClient()) return null;
        return new Trait();
    }

    @Environment(EnvType.CLIENT)
    class Trait extends ItemTraitImpl<BoatItem, BoatRendererTrait> implements BoatRendererTrait {

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void afterItemRegistration(
                BoatItem item,
                ItemDefinition<BoatItem, ? extends ItemDefinition<BoatItem, ?>> definition
        ) {
            if (definition instanceof BoatItemDefinition<?> boatDefinition) {
                final var modelLocation = new ModelLayerLocation(
                        definition.itemKey.location().withPrefix("boat/"),
                        "main"
                );

                EntityModelLayerRegistry.registerModelLayer(
                        modelLocation,
                        () -> BoatModel.createBoatModel()
                );

                EntityRendererRegistry.register(
                        boatDefinition.entityType(),
                        (context) -> new BoatRenderer(context, modelLocation)
                );
            } else {
                throw new IllegalStateException("BoatRendererTrait can only be used with BoatItemDefinition");
            }
        }
    }
}
