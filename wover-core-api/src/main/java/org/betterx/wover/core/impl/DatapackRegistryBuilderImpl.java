package org.betterx.wover.core.impl;

import org.betterx.wover.core.api.DatapackRegistryBuilder;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DatapackRegistryBuilderImpl {
    private static final List<Entry<?>> REGISTRIES = new LinkedList<>();

    private record Entry<T>(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap) {

        public BootstapContext<T> getContext(WritableRegistry<T> registry) {
            return DatapackRegistryBuilder.getContext(null, registry);
        }
    }


    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        REGISTRIES.add(new Entry<>(key, elementCodec, bootstrap));
    }

    public static void forEach(BiConsumer<ResourceKey<? extends Registry<?>>, Codec<?>> consumer) {
        REGISTRIES.forEach(entry -> {
            consumer.accept(entry.key, entry.elementCodec);
        });
    }

    public static <E> void bootstrap(
            ResourceKey<? extends Registry<E>> resourceKey,
            WritableRegistry<E> writableRegistry
    ) {
        REGISTRIES.forEach(entry -> {
            if (entry.key.equals(resourceKey)) {
                entry.bootstrap.accept(entry.getContext((WritableRegistry) writableRegistry));
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
