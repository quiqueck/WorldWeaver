package de.ambertation.wover.core.api.registry;

import de.ambertation.wover.core.impl.registry.DatapackLoadElementImpl;
import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;

import com.mojang.serialization.Codec;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.stream.Stream;
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
            // Minecraft 26.3 removed BootstrapContext#register(ResourceKey, T, Lifecycle); the interface
            // now only declares the two-argument register(ResourceKey, T) and every registration uses the
            // registry's own lifecycle. The lifecycle-aware overload we used to implement here is
            // therefore unreachable and was dropped - but its extra "re-register a declared-but-unbound
            // holder" branch was real behaviour that the two-argument version did not have, so it was
            // folded in below. Datapack loading pre-creates unbound Holder.References for keys that are
            // referenced before they are defined; without this branch such an element would silently stay
            // unbound after a bootstrap wrote it.
            @Override
            public Holder.Reference<T> register(
                    ResourceKey<T> resourceKey,
                    T object
            ) {
                if (!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, object, RegistrationInfo.BUILT_IN);
                } else {
                    var holder = registry.getOrThrow(resourceKey);
                    if (!holder.isBound()) {
                        return registry.register(resourceKey, object, RegistrationInfo.BUILT_IN);
                    }
                    return holder;
                }
            }

            // 26.3: RegistryOps.RegistryInfoLookup#lookup returns the Optional<HolderGetter<S>> directly.
            // The RegistryOps.RegistryInfo wrapper (with its getter()/owner() accessors) is gone.
            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return registryInfoLookup.lookup(resourceKey).orElse(null);
            }

            // New abstract method in 26.3 (on the extracted BootstrapContextAccess super-interface).
            // Vanilla implements it as `contextRegistries.lookupOrThrow(key).listElements()`, i.e. "list
            // everything that is visible in the surrounding context for that registry".
            // A RegistryOps.RegistryInfoLookup only hands out a HolderGetter, and not every getter is
            // enumerable: for a registry that is already loaded this is the registry's own
            // HolderLookup.RegistryLookup, but for a registry that is still being loaded concurrently
            // RegistryDataLoader hands out a ConcurrentHolderGetter, which can resolve single keys/tags
            // but deliberately cannot list its (still incomplete) contents. We therefore enumerate when
            // the getter supports it and return an empty stream otherwise, rather than throwing.
            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(
                    ResourceKey<? extends Registry<? extends S>> resourceKey
            ) {
                final HolderGetter<S> getter = registryInfoLookup.lookup(resourceKey).orElse(null);
                if (getter instanceof HolderLookup<S> holderLookup) {
                    return holderLookup.listElements();
                }
                return Stream.empty();
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
            // See makeContext(RegistryOps.RegistryInfoLookup, WritableRegistry): 26.3 dropped the
            // lifecycle-aware register(ResourceKey, T, Lifecycle) from BootstrapContext. Here the two
            // overloads only ever differed in the RegistrationInfo they passed on, so removing it does
            // not change any behaviour.
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

            // New abstract method in 26.3 (see BootstrapContextAccess). Mirrors lookup(...) above:
            // with a RegistryAccess every registry is a HolderLookup and can be enumerated directly;
            // without one, only the registry that is currently being bootstrapped is visible. We
            // enumerate that one through the WritableRegistry itself, because the registration lookup
            // stored in `lookup` is only a HolderGetter and cannot list its elements.
            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(
                    ResourceKey<? extends Registry<? extends S>> resourceKey
            ) {
                if (access == null) {
                    if (resourceKey.equals(registry.key())) {
                        return (Stream<Holder.Reference<S>>) (Stream<?>) registry.listElements();
                    }
                    return Stream.empty();
                }
                return access.lookupOrThrow(resourceKey).listElements();
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
