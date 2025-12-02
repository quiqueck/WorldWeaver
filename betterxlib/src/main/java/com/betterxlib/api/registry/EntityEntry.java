package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * A wrapper around a registered entity type that provides convenient access methods.
 *
 * @param <T> the entity type
 */
public class EntityEntry<T extends Entity> implements Supplier<EntityType<T>> {
    private final DeferredHolder<EntityType<?>, EntityType<T>> entityType;
    private final ResourceLocation id;

    EntityEntry(DeferredHolder<EntityType<?>, EntityType<T>> entityType, ResourceLocation id) {
        this.entityType = entityType;
        this.id = id;
    }

    @Override
    public EntityType<T> get() {
        return entityType.get();
    }

    /**
     * Get the resource location (ID) of this entity type.
     *
     * @return the resource location
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Get the deferred holder.
     *
     * @return the deferred holder
     */
    public DeferredHolder<EntityType<?>, EntityType<T>> getDelegate() {
        return entityType;
    }

    /**
     * Check if an entity is of this type.
     *
     * @param entity the entity to check
     * @return true if the entity is of this type
     */
    public boolean is(Entity entity) {
        return entity.getType() == get();
    }
}
