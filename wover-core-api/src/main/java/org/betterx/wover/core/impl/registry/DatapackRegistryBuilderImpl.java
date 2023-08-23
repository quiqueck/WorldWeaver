package org.betterx.wover.core.impl.registry;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class DatapackRegistryBuilderImpl {
    public static final int DEFAULT_PRIORITY = 1000;
    public static final int MAX_READONLY_PRIORITY = -1000;
    private static final PriorityLinkedList<Entry<?>> REGISTRIES = new PriorityLinkedList<>();

    private record Entry<T>(
            ResourceKey<? extends Registry<T>> key,
            @Nullable
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap) {

        public BootstapContext<T> getContext(
                RegistryOps.RegistryInfoLookup registryInfoLookup,
                WritableRegistry<T> registry
        ) {
            return DatapackRegistryBuilder.makeContext(registryInfoLookup, registry);
        }

        public boolean definesRegistry() {
            return elementCodec != null;
        }
    }

    public static boolean isRegistered(ResourceLocation registryId) {
        return REGISTRIES.stream()
                         .filter(entry -> entry.definesRegistry())
                         .anyMatch(entry -> entry.key.location().equals(registryId));
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        register(key, bootstrap, DEFAULT_PRIORITY);
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap,
            int priority
    ) {
        REGISTRIES.add(new Entry<>(key, null, bootstrap), priority);
    }

    public static <T> void registerReadOnly(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        register(key, bootstrap, DEFAULT_PRIORITY);
    }

    public static <T> void registerReadOnly(
            ResourceKey<? extends Registry<T>> key,
            Consumer<BootstapContext<T>> bootstrap,
            int priority
    ) {
        register(key, bootstrap, MAX_READONLY_PRIORITY + priority);
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        register(key, elementCodec, DEFAULT_PRIORITY, bootstrap);
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            int priority,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        if (isRegistered(key.location())) {
            throw new IllegalStateException("Registry with id " + key.location() + " was already registered!");
        }

        REGISTRIES.add(new Entry<>(key, elementCodec, bootstrap), priority);
    }

    public static void forEach(BiConsumer<ResourceKey<? extends Registry<?>>, Codec<?>> consumer) {
        REGISTRIES.forEach(entry -> {
            consumer.accept(entry.key, entry.elementCodec);
        });
    }

    public static <E> void bootstrap(
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            ResourceKey<? extends Registry<E>> resourceKey,
            WritableRegistry<E> writableRegistry
    ) {
        REGISTRIES.forEach(entry -> {
            if (entry.key.equals(resourceKey)) {
                entry.bootstrap.accept(entry.getContext(registryInfoLookup, (WritableRegistry) writableRegistry));
            }
        });
    }

    public static void bootstrap(
            BiConsumer<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryBootstrap<? extends Object>> consumer
    ) {
        REGISTRIES.forEach(entry -> {
            consumer.accept(
                    entry.key,
                    (ctx) -> {
                        entry.bootstrap.accept((BootstapContext) ctx);
                    }
            );
        });
    }
}
