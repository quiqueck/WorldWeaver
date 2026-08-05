package de.ambertation.wover.core.api.registry;

import de.ambertation.wover.core.impl.registry.DatapackLoadElementImpl;
import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * The DatapackRegistryBuilder adds custom, Datapack-Backed Registries and allows you
 * to add a bootstrap functions to any Registry. The bootstrap function is
 * called whenever the Registry is loaded from a Datapack.
 * <p>
 * In order to ensure that the Registry is loaded correctly, you need to call the register
 * and addBootstrap methods from the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
 * {@link DatapackRegistryEntrypoint} interface.
 */
public class DatapackRegistryBuilder {
    /**
     * Register a new, custom Registry.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
     *
     * @param key          The ResourceKey of the Registry
     * @param elementCodec The Codec used to serialize the Registry
     * @param bootstrap    The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>          The type of the Registry-Elements
     */
    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, elementCodec, bootstrap);
    }

    /**
     * Register a new, custom Registry.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
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
            Consumer<BootstrapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.register(key, elementCodec, priority, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param priority  The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.register(key, bootstrap, priority);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addReadOnlyBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.registerReadOnly(key, bootstrap);
    }

    /**
     * Add a method that will be called whenever the Registry is
     * loaded from a Datapack. This includes custom Registries added using
     * {@link #register(ResourceKey, Codec, Consumer)}, as well as vanilla
     * Registries.
     * <p>
     * In order to ensure that the Registry is loaded correctly, you need to call this method from
     * the <code>wover.datapack.registry</code> Entrypoint. The Entrypoint must implement the
     * {@link DatapackRegistryEntrypoint} interface.
     *
     * @param key       The ResourceKey of the Registry
     * @param bootstrap The bootstrap function, which is called when the Registry is loaded from a DataPack
     * @param priority  The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
     * @param <T>       The type of the Registry-Elements
     */
    public static <T> void addReadOnlyBootstrap(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap,
            int priority
    ) {
        DatapackRegistryBuilderImpl.registerReadOnly(key, bootstrap, priority);
    }

    /**
     * Checks if a Registry with the given {@link Identifier} was created with our
     * {@link DatapackRegistryBuilder}.
     *
     * @param registryId The {@link Identifier} of the Registry
     * @return {@code true} if the Registry is registered, {@code false} otherwise
     */
    public static boolean isRegistered(Identifier registryId) {
        return DatapackRegistryBuilderImpl.isRegistered(registryId);
    }

    /**
     * Returns a BootstrapContext for the given Registry.
     *
     * @param registryInfoLookup The RegistryInfoLookup used to lookup Registry-Infos
     * @param registry           The Registry
     * @param <T>                The type of the Registry-Elements
     * @return A BootstrapContext for the given Registry
     */
    public static <T> BootstrapContext<T> makeContext(
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            WritableRegistry<T> registry
    ) {
        return new BootstrapContext<>() {
            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object,
                    Lifecycle lifecycle
            ) {
                if (!registry.containsKey(resourceKey)) {
                    //TODO: 1.21 creating a new instance of RegistrationInfo might be expensive...
                    return registry.register(resourceKey, object, new RegistrationInfo(Optional.empty(), lifecycle));
                } else {
                    var holder = registry.getOrThrow(resourceKey);
                    if (!holder.isBound()) {
                        return registry.register(
                                resourceKey,
                                object,
                                new RegistrationInfo(Optional.empty(), lifecycle)
                        );
                    }
                    return holder;
                }
            }

            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object
            ) {
                if (!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, object, RegistrationInfo.BUILT_IN);
                } else {
                    return registry.getOrThrow(resourceKey);
                }
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return registryInfoLookup.lookup(resourceKey).map(o -> o.getter()).orElse(null);
            }
        };
    }

    /**
     * Returns a BootstrapContext for the given Registry.
     *
     * @param access   The RegistryAccess used to lookup Registry-Infos, if {@code null}
     *                 only the passed registry can be looked up
     * @param registry The Registry
     * @param <T>      The type of the Registry-Elements
     * @return A BootstrapContext for the given Registry
     */
    public static <T> BootstrapContext<T> makeContext(
            @Nullable RegistryAccess access,
            WritableRegistry<T> registry
    ) {
        var lookup = access == null ? registry.createRegistrationLookup() : null;
        return new BootstrapContext<>() {
            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object,
                    Lifecycle lifecycle
            ) {
                if (!registry.containsKey(resourceKey)) {
                    //TODO: 1.21 creating a new instance of RegistrationInfo might be expensive...
                    return registry.register(resourceKey, object, new RegistrationInfo(Optional.empty(), lifecycle));
                } else {
                    return registry.getOrThrow(resourceKey);
                }
            }


            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object
            ) {
                if (!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, object, RegistrationInfo.BUILT_IN);
                } else {
                    return registry.getOrThrow(resourceKey);
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

    /**
     * Creates a new ResourceKey for a Registry.
     *
     * @param location The location of the Registry
     * @param <T>      The type of the Elements stored in the Registry
     * @return A new ResourceKey for a Registry
     */
    public static <T> ResourceKey<Registry<T>> createRegistryKey(Identifier location) {
        return ResourceKey.createRegistryKey(location);
    }

    /**
     * Registers a watcher that is notified whenever an element of the given registry was loaded from a
     * Datapack.
     *
     * @param registryKey The ResourceKey of the Registry
     * @param watcher     The watcher that is notified when an element was loaded
     * @param <E>         The type of the Registry-Elements
     */
    public static <E> void onElementLoad(
            ResourceKey<Registry<E>> registryKey,
            OnElementLoad<E> watcher
    ) {
        DatapackLoadElementImpl.register(registryKey, watcher);
    }

    /**
     * Registers a watcher that is notified whenever an element of the given registry was loaded from a
     * Datapack.
     *
     * @param registryKey The ResourceKey of the Registry
     * @param watcher     The watcher that is notified when an element was loaded
     * @param priority    The priority of the watcher. The higher the priority, the earlier the watcher is notified
     * @param <E>         The type of the Registry-Elements
     */
    public static <E> void onElementLoad(
            ResourceKey<Registry<E>> registryKey,
            OnElementLoad<E> watcher,
            int priority
    ) {
        DatapackLoadElementImpl.register(registryKey, watcher, priority);
    }
}
