package org.betterx.wover.core.api.registry;

import org.betterx.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * The DatapackRegistryBuilder adds custom, Datapack-Backed Registries and allows you
 * to add a bootstrap functions to any Registry. The bootstrap function is
 * called whenever the Registry is loaded from a Datapack.
 */
public class DatapackRegistryBuilder {
    /**
     * Register a new, custom Registry.
     *
     * @param key          The ResourceKey of the Registry
     * @param elementCodec The Codec used to serialize the Registry
     * @param bootstrap    The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>          The type of the Registry-Elements
     */
    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, elementCodec, bootstrap);
    }

    /**
     * Register a new, custom Registry.
     *
     * @param key          The ResourceKey of the Registry
     * @param elementCodec The Codec used to serialize the Registry
     * @param priority     The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
     * @param bootstrap    The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>          The type of the Registry-Elements
     */
    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.register(key, elementCodec, priority, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param priority  The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.register(key, bootstrap, priority);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addReadOnlyBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.registerReadOnly(key, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param priority  The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addReadOnlyBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.registerReadOnly(key, bootstrap, priority);
    }

    /**
     * Checks if a Registry with the given {@link ResourceLocation} was created with our
     * {@link DatapackRegistryBuilder}.
     *
     * @param registryId The {@link ResourceLocation} of the Registry
     * @return {@code true} if the Registry is registered, {@code false} otherwise
     */
    public static boolean isRegistered(ResourceLocation registryId) {
        return DatapackRegistryBuilderImpl.isRegistered(registryId);
    }

    /**
     * Returns a BootstapContext for the given Registry.
     *
     * @param registryInfoLookup The RegistryInfoLookup used to lookup Registry-Infos
     * @param registry           The Registry
     * @param <T>                The type of the Registry-Elements
     * @return A BootstapContext for the given Registry
     */
    public static <T> BootstapContext<T> makeContext(
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            WritableRegistry<T> registry
    ) {
        return new BootstapContext<>() {
            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object,
                    Lifecycle lifecycle
            ) {
                if (!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, object, lifecycle);
                } else {
                    return registry.getHolderOrThrow(resourceKey);
                }
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return registryInfoLookup.lookup(resourceKey).map(o -> o.getter()).orElse(null);
            }
        };
    }

    /**
     * Returns a BootstapContext for the given Registry.
     *
     * @param access   The RegistryAccess used to lookup Registry-Infos, if {@code null}
     *                 only the passed registry can be looked up
     * @param registry The Registry
     * @param <T>      The type of the Registry-Elements
     * @return A BootstapContext for the given Registry
     */
    public static <T> BootstapContext<T> makeContext(
            @Nullable RegistryAccess access,
            WritableRegistry<T> registry
    ) {
        var lookup = access == null ? registry.createRegistrationLookup() : null;
        return new BootstapContext<>() {
            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object,
                    Lifecycle lifecycle
            ) {
                if (!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, object, lifecycle);
                } else {
                    return registry.getHolderOrThrow(resourceKey);
                }
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                if (access == null) {
                    if (resourceKey.equals(registry.key())) {
                        return (HolderGetter<S>) lookup;
                    }
                    return null;
                }
                return access.lookupOrThrow(resourceKey);
            }
        };
    }

    public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation location) {
        return ResourceKey.createRegistryKey(location);
    }
}
