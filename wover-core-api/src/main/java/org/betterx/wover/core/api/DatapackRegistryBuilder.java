package org.betterx.wover.core.api;

import org.betterx.wover.core.impl.DatapackRegistryBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class DatapackRegistryBuilder {
    public static <T> void register(
            ResourceKey<? extends Registry<T>> key,
            Codec<T> elementCodec,
            Consumer<BootstapContext<T>> bootstrap
    ) {
        DatapackRegistryBuilderImpl.register(key, elementCodec, bootstrap);
    }

    public static <T> BootstapContext<T> getContext(
            @Nullable RegistryAccess access,
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
                if (access == null) {
                    if (resourceKey.equals(registry.key())) {
                        return (HolderGetter<S>) registry.createRegistrationLookup();
                    }
                    return null;
                }
                return access.lookupOrThrow(resourceKey);
            }
        };
    }
}
