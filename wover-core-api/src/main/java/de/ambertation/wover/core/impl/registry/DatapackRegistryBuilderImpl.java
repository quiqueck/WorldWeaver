package de.ambertation.wover.core.impl.registry;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.entrypoint.LibWoverCore;
import de.ambertation.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import net.fabricmc.loader.api.FabricLoader;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class DatapackRegistryBuilderImpl {
    public static final int DEFAULT_PRIORITY = 1000;
    public static final int MAX_READONLY_PRIORITY = -1000;
    private static final PriorityLinkedList<Entry<?>> REGISTRIES = new PriorityLinkedList<>();

    private record Entry<T>(
            ResourceKey<? extends Registry<T>> key,
            @Nullable
            Codec<T> elementCodec,
            Consumer<BootstrapContext<T>> bootstrap) {

        public BootstrapContext<T> getContext(
                RegistryOps.RegistryInfoLookup registryInfoLookup,
                WritableRegistry<T> registry
        ) {
            return DatapackRegistryBuilder.makeContext(registryInfoLookup, registry);
        }

        public boolean definesRegistry() {
            return elementCodec != null;
        }
    }


    public static boolean isRegistered(Identifier registryId) {
        return REGISTRIES.stream()
                         .filter(entry -> entry.definesRegistry())
                         .anyMatch(entry -> entry.key.identifier().equals(registryId));
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        register(key, bootstrap, DEFAULT_PRIORITY);
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap,
            int priority
    ) {
        LibWoverCore.C.log.debug("Adding dynamic registry bootstrap: " + key.identifier());
        REGISTRIES.add(new Entry<>(key, null, bootstrap), Math.max(MAX_READONLY_PRIORITY + 1, priority));
    }

    public static <T> void registerReadOnly(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        register(key, bootstrap, MAX_READONLY_PRIORITY);
    }

    public static <T> void registerReadOnly(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstrapContext<T>> bootstrap,
            int priority
    ) {
        register(key, bootstrap, Math.min(MAX_READONLY_PRIORITY, Integer.MIN_VALUE + priority));
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        register(key, elementCodec, DEFAULT_PRIORITY, bootstrap);
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            int priority,
            Consumer<BootstrapContext<T>> bootstrap
    ) {
        if (isRegistered(key.identifier())) {
            throw new IllegalStateException("Registry with id " + key.identifier() + " was already registered!");
        }

        LibWoverCore.C.log.debug("Adding dynamic registry: " + key.identifier());
        REGISTRIES.add(new Entry<>(key, elementCodec, bootstrap), priority);
    }

    public static void forEach(BiConsumer<ResourceKey<? extends Registry<?>>, Codec<?>> consumer) {
        initEntrypoints();
        REGISTRIES.forEach(entry -> {
            consumer.accept(entry.key, entry.elementCodec);
        });
    }

    private static boolean didInitEntrypoints = false;

    private static void initEntrypoints() {
        if (didInitEntrypoints) {
            return;
        }
        didInitEntrypoints = true;
        LibWoverCore.C.LOG.verbose("Processing wover.datapack.registry Entrypoints");
        FabricLoader.getInstance().getEntrypoints("wover.datapack.registry", DatapackRegistryEntrypoint.class)
                    .forEach(entrypoint -> {
                        LibWoverCore.C.LOG.verbose("    - Processing Entrypoint: {}", entrypoint.getClass().getName());
                        entrypoint.registerDatapackRegistries();
                    });
    }

    @ApiStatus.Internal
    public static <E> void bootstrap(
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            ResourceKey<? extends Registry<E>> resourceKey,
            WritableRegistry<E> writableRegistry
    ) {
        initEntrypoints();
        LibWoverCore.C.LOG.debug("Bootstrapping registry {}", resourceKey.identifier());
        REGISTRIES.forEach(entry -> {
            if (entry.key.equals(resourceKey)) {
                LibWoverCore.C.LOG.debug("Calling custom Registry Bootstrap on {}", resourceKey.identifier());
                entry.bootstrap.accept(entry.getContext(registryInfoLookup, (WritableRegistry) writableRegistry));
            }
        });
    }

    // I think we do not need to inject any data into the vanilla registries
    // so we can just ignore this method.
    // VanillaRegistries are apparently only used in the vanilla datagen, debug mode and in the validate command
    @ApiStatus.Internal
    public static void bootstrap(
            BiConsumer<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryBootstrap<? extends Object>> consumer
    ) {
        LibWoverCore.C.LOG.verboseWarning("DID NOT bootstrap VanillaRegistries.");
//        initEntrypoints();
//        LibWoverCore.C.LOG.debug("Bootstrapping vanilla registries");
//        REGISTRIES.forEach(entry -> {
//            LibWoverCore.C.LOG.debug("Calling custom vanilla Registry Bootstrap on {}", entry.key);
//            consumer.accept(
//                    entry.key,
//                    (ctx) -> {
//                        entry.bootstrap.accept((BootstrapContext) ctx);
//                    }
//            );
//        });
    }
}
