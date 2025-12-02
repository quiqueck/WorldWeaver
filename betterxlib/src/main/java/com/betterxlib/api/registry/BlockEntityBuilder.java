package com.betterxlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Builder for registering block entity types with a fluent API.
 *
 * @param <T> the block entity type
 */
public class BlockEntityBuilder<T extends BlockEntity> {
    private final BXRegistry registry;
    private final String name;
    private final Supplier<BlockEntityType<T>> typeSupplier;

    BlockEntityBuilder(BXRegistry registry, String name, Supplier<BlockEntityType<T>> typeSupplier) {
        this.registry = registry;
        this.name = name;
        this.typeSupplier = typeSupplier;
    }

    /**
     * Complete the registration and return the deferred holder.
     *
     * @return the deferred block entity type holder
     */
    @SuppressWarnings("unchecked")
    public DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register() {
        return (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>) (DeferredHolder<?, ?>)
            registry.getBlockEntities().register(name, typeSupplier);
    }
}
