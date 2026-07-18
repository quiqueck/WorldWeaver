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
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.RaftRenderer;
import net.minecraft.world.item.BoatItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import org.jetbrains.annotations.Nullable;


public class BoatRendererTraitBuilder extends AbstractItemTraitBuilder<BoatItem, BoatRendererTrait> implements BoatRendererTrait.Builder {
    public static final BoatRendererTrait.Builder BUILDER = new BoatRendererTraitBuilder();

    protected BoatRendererTraitBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverItem.C, "boat_model"));

    }

    @Override
    public BoatRendererTrait withDefault() {
        if (!ModCore.isClient()) return null;
        return new Trait(false, false);
    }


    public @Nullable BoatRendererTrait with(boolean withChest, boolean isRaft) {
        if (!ModCore.isClient()) return null;
        return new Trait(withChest, isRaft);
    }

    @Environment(EnvType.CLIENT)
    class Trait extends ItemTraitImpl<BoatItem, BoatRendererTrait> implements BoatRendererTrait {
        private final boolean withChest;
        private final boolean isRaft;

        Trait(boolean withChest, boolean isRaft) {
            this.withChest = withChest;
            this.isRaft = isRaft;
        }

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
                // rafts still use the "boat"/"chest_boat" texture folders, matching vanilla's bamboo raft
                final var modelLocation = new ModelLayerLocation(
                        definition.itemKey.location()
                                          .withPrefix(withChest ? "chest_boat/" : "boat/"),
                        "main"
                );

                EntityModelLayerRegistry.registerModelLayer(
                        modelLocation,
                        isRaft
                                ? (withChest ? RaftModel::createChestRaftModel : RaftModel::createRaftModel)
                                : (withChest ? BoatModel::createChestBoatModel : BoatModel::createBoatModel)
                );

                EntityRendererRegistry.register(
                        boatDefinition.entityType(),
                        isRaft
                                ? (context) -> new RaftRenderer(context, modelLocation)
                                : (context) -> new BoatRenderer(context, modelLocation)
                );
            } else {
                throw new IllegalStateException("BoatRendererTrait can only be used with BoatItemDefinition");
            }
        }

        @Override
        public boolean withChest() {
            return this.withChest;
        }

        @Override
        public boolean isRaft() {
            return this.isRaft;
        }
    }

}
