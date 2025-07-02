package org.betterx.wover.item.api;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.level.Level;

public class BoatItemDefinition<I extends BoatItem> extends ItemDefinition<I, BoatItemDefinition<I>> {
    public record BoatType(
            EntityType<? extends AbstractBoat> entityType,
            BoatItem item
    ) {
    }

    /**
     * Factory interface for creating boat items from configuration objects.
     * Extends the base ItemFactory to work specifically with BoatItemDefinition.
     *
     * @param <I> The type of boat item to create
     */
    public interface ItemFactory<I extends BoatItem> extends ItemDefinition.ItemFactory<I, BoatItemDefinition<I>> {
    }

    private EntityType<? extends AbstractBoat> entityType;
    private BoatItem boatItem;

    protected BoatItemDefinition(
            ItemRegistry registry,
            String itemName,
            ItemFactory<I> itemFactory
    ) {
        super(registry, itemName, itemFactory);
    }

    Boat boatFactory(EntityType<Boat> entityType, Level level) {
        return new Boat(entityType, level, () -> this.boatItem);
    }

    @Override
    protected void beforeBuild() {
        properties.stacksTo(1);

        final var entityKey = registry.entityKey(itemKey);
        this.entityType = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                entityKey,
                EntityType.Builder
                        .of(this::boatFactory, MobCategory.MISC)
                        .noLootTable()
                        .sized(1.375F, 0.5625F)
                        .eyeHeight(0.5625F)
                        .clientTrackingRange(10)
                        .build(entityKey)
        );
    }

    @Override
    protected I beforeRegister(I item) {
        this.boatItem = item;
        return item;
    }

    public EntityType<? extends AbstractBoat> entityType() {
        return this.entityType;
    }


    public BoatType buildAndRegisterBoat() {
        var item = super.buildAndRegister();
        return new BoatType(this.entityType, item);
    }
}
