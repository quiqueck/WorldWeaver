package org.betterx.wover.core.api;

import org.betterx.wover.core.impl.DatapackRegistryBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * The DatapackRegistryBuilder can be used to get notified when a builtin Registry
 * is loaded from a Datapack.
 * <p>
 * It also allows you to add custom Registries to the Datapack-Loading procedure.
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
     * Registers a method that will be called whenever the Registry is
     * loaded from a Datapack.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void registerNotification(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, bootstrap);
    }

    /**
     * Returns a BootstapContext for the given Registry.
     *
     * @param registryInfoLookup The RegistryInfoLookup used to lookup Registry-Infos
     * @param registry           The Registry
     * @param <T>                The type of the Registry-Elements
     * @return A BootstapContext for the given Registry
     */
    public static <T> BootstapContext<T> getContext(
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
    public static <T> BootstapContext<T> getContext(
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
}
