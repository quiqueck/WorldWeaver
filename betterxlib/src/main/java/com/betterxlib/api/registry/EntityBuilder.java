package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Builder for registering entity types with a fluent API.
 *
 * @param <T> the entity type
 */
public class EntityBuilder<T extends Entity> {
    private final BXRegistry registry;
    private final String name;
    private final Supplier<EntityType<T>> typeSupplier;

    EntityBuilder(BXRegistry registry, String name, Supplier<EntityType<T>> typeSupplier) {
        this.registry = registry;
        this.name = name;
        this.typeSupplier = typeSupplier;
    }

    /**
     * Complete the registration and return the EntityEntry.
     *
     * @return the registered entity entry
     */
    @SuppressWarnings("unchecked")
    public EntityEntry<T> register() {
        ResourceLocation id = registry.id(name);
        DeferredHolder<EntityType<?>, EntityType<T>> holder =
            (DeferredHolder<EntityType<?>, EntityType<T>>) (DeferredHolder<?, ?>)
                registry.getEntities().register(name, typeSupplier);

        return new EntityEntry<>(holder, id);
    }
}
