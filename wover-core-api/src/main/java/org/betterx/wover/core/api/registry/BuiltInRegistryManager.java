package org.betterx.wover.core.api.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

public class BuiltInRegistryManager {

    public static <T> Registry<T> register(
            ResourceKey<? extends Registry<T>> resourceKey,
            Function<Registry<T>, T> registryBootstrap
    ) {
        return BuiltInRegistries.registerSimple(resourceKey, registryBootstrap::apply);
    }

    public static <T> Registry<T> register(
            ResourceKey<? extends Registry<T>> resourceKey,
            Lifecycle lifecycle,
            Function<Registry<T>, T> registryBootstrap
    ) {
        return BuiltInRegistries.registerSimple(resourceKey, lifecycle, registryBootstrap::apply);
    }
}
