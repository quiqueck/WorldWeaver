package de.ambertation.wover.item.impl.client.render;

import de.ambertation.wover.item.api.render.BoatRendererBinding;
import de.ambertation.wover.item.api.trait.ItemTrait;

import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.object.boat.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.RaftRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * Client-side applier for {@link BoatRendererBinding}. Walks the item registry once at client init and, for each
 * boat item carrying a boat-renderer binding, registers its model layer and entity renderer. This is the body the
 * former {@code BoatRendererTrait.afterItemRegistration(...)} ran per item during registration; the entity type
 * (registered by {@code BoatItemDefinition} under the item's own location) is resolved from the registry here
 * instead of from the definition. Registered via {@code wover.client.traits}.
 */
@Environment(EnvType.CLIENT)
public final class ClientBoatRenderBootstrap {
    private ClientBoatRenderBootstrap() {
    }

    /**
     * Registers renderers/model layers for every registered boat item carrying a boat-renderer binding.
     */
    public static void applyBoatRenderers() {
        for (Item item : BuiltInRegistries.ITEM) {
            ItemTrait.runtimeTraits(item)
                     .filter(trait -> trait.is(BoatRendererBinding.BOAT_RENDERER_KEY))
                     .forEach(trait -> {
                         if (trait instanceof BoatRendererBinding binding) {
                             apply(item, binding);
                         }
                     });
        }
    }

    @SuppressWarnings("unchecked")
    private static void apply(Item item, BoatRendererBinding binding) {
        final boolean withChest = binding.withChest();
        final boolean isRaft = binding.isRaft();

        // rafts still use the "boat"/"chest_boat" texture folders, matching vanilla's bamboo raft
        final var modelLocation = new ModelLayerLocation(
                BuiltInRegistries.ITEM.getKey(item)
                                      .withPrefix(withChest ? "chest_boat/" : "boat/"),
                "main"
        );

        ModelLayerRegistry.registerModelLayer(
                modelLocation,
                isRaft
                        ? (withChest ? RaftModel::createChestRaftModel : RaftModel::createRaftModel)
                        : (withChest ? BoatModel::createChestBoatModel : BoatModel::createBoatModel)
        );

        final EntityType<? extends AbstractBoat> entityType =
                (EntityType<? extends AbstractBoat>) BuiltInRegistries.ENTITY_TYPE.getValue(BuiltInRegistries.ITEM.getKey(item));

        EntityRendererRegistry.register(
                entityType,
                isRaft
                        ? (context) -> new RaftRenderer(context, modelLocation)
                        : (context) -> new BoatRenderer(context, modelLocation)
        );
    }
}
