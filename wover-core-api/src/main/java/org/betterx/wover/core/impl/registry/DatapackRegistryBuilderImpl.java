package org.betterx.wover.core.impl.registry;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class DatapackRegistryBuilderImpl {
    private static final List<Entry<?>> REGISTRIES = new LinkedList<>();

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
        REGISTRIES.add(new Entry<>(key, null, bootstrap));
    }

    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        if (isRegistered(key.location())) {
            throw new IllegalStateException("Registry with id " + key.location() + " was already registered!");
        }

        REGISTRIES.add(new Entry<>(key, elementCodec, bootstrap));
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
