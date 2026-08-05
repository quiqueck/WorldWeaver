package de.ambertation.wover.core.api.registry;

import de.ambertation.wover.entrypoint.LibWoverCore;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * Helper methods to register elements into vanilla {@link Registry} instances (such as
 * {@link BuiltInRegistries}), and to create new, code-defined (built-in) registries.
 * <p>
 * This class is only useful for registries that are populated from code. Registries that should be populated
 * from Datapack JSON files should be created with {@link DatapackRegistryBuilder} instead.
 */
public class BuiltInRegistryManager {
    /**
     * Registers a new element into the given registry. Thin wrapper around
     * {@link Registry#register(Registry, Identifier, Object)}.
     *
     * @param registry         The registry to register the element into.
     * @param resourceLocation The id of the new element.
     * @param object           The element to register.
     * @param <V>              The value type of the registry.
     * @param <T>              The type of the element that is registered.
     * @return The registered element.
     */
    public static <V, T extends V> T register(Registry<V> registry, Identifier resourceLocation, T object) {
        return Registry.register(registry, resourceLocation, object);
    }

    /**
     * Registers a new element into the given registry. Thin wrapper around
     * {@link Registry#register(Registry, ResourceKey, Object)}.
     *
     * @param registry     The registry to register the element into.
     * @param resourceKey  The key of the new element.
     * @param object       The element to register.
     * @param <V>          The value type of the registry.
     * @param <T>          The type of the element that is registered.
     * @return The registered element.
     */
    public static <V, T extends V> T register(Registry<V> registry, ResourceKey<V> resourceKey, T object) {
        return Registry.register(registry, resourceKey, object);
    }

    /**
     * Registers a new element into the given registry and returns a {@link Holder.Reference} to it. Thin wrapper
     * around {@link Registry#registerForHolder(Registry, Identifier, Object)}.
     *
     * @param registry         The registry to register the element into.
     * @param resourceLocation The id of the new element.
     * @param object           The element to register.
     * @param <V>              The value type of the registry.
     * @param <T>              The type of the element that is registered.
     * @return A {@link Holder.Reference} pointing to the registered element.
     */
    public static <V, T extends V> Holder.Reference<V> registerForHolder(
            Registry<V> registry,
            Identifier resourceLocation,
            T object
    ) {
        return Registry.registerForHolder(registry, resourceLocation, object);
    }

    /**
     * Creates a new, built-in {@link Registry} and adds it to {@link BuiltInRegistries}.
     * <p>
     * Use this method for registries that are populated entirely from code. For registries that should be
     * populated from Datapack JSON files, use {@link DatapackRegistryBuilder#register} instead.
     *
     * @param resourceKey       The key that identifies the new registry.
     * @param registryBootstrap A function that populates the registry and returns its default element.
     * @param <T>               The type of the elements stored in the registry.
     * @return The newly created registry.
     */
    public static <T> Registry<T> createRegistry(
            ResourceKey<? extends Registry<T>> resourceKey,
            Function<Registry<T>, T> registryBootstrap
    ) {
        LibWoverCore.C.log.debug("Creating registry: " + resourceKey.identifier());
        return BuiltInRegistries.registerSimple(resourceKey, registryBootstrap::apply);
    }

    /**
     * Creates a new, built-in {@link Registry} and adds it to {@link BuiltInRegistries}.
     *
     * @param resourceKey       The key that identifies the new registry.
     * @param lifecycle         Unused, kept only for backwards compatibility.
     * @param registryBootstrap A function that populates the registry and returns its default element.
     * @param <T>               The type of the elements stored in the registry.
     * @return The newly created registry.
     * @deprecated Use {@link #createRegistry(ResourceKey, Function)} instead, the {@link Lifecycle} parameter is
     * not used.
     */
    @Deprecated
    public static <T> Registry<T> createRegistry(
            ResourceKey<? extends Registry<T>> resourceKey,
            Lifecycle lifecycle,
            Function<Registry<T>, T> registryBootstrap
    ) {
        LibWoverCore.C.log.debug("Creating registry: " + resourceKey.identifier());
        return BuiltInRegistries.registerSimple(resourceKey, registryBootstrap::apply);
    }
}
