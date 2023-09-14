package org.betterx.wover.core.impl.registry;

import org.betterx.wover.core.api.registry.CustomBootstrapContext;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CustomBootstrapContextImpl<T> {
    private static class ContextData<T, C extends CustomBootstrapContext<T, C>> {
        private HolderGetter<T> lastGetter = null;
        private C bootstrapContext = null;

    }

    private static final Map<ResourceKey<Registry<?>>, ContextData<?, ?>> CONTEXT_OBJECTS = new HashMap<>();


    @SuppressWarnings("unchecked")
    private static <T, C extends CustomBootstrapContext<T, C>> @NotNull ContextData<T, C> getContextObject(
            ResourceKey<Registry<T>> registryKey
    ) {
        return (ContextData<T, C>) CONTEXT_OBJECTS.computeIfAbsent(
                (ResourceKey<Registry<?>>) (Object) registryKey,
                key -> new ContextData<>()
        );
    }

    public static <B, T, C extends CustomBootstrapContext<T, C>> @Nullable C initContext(
            @Nullable BootstapContext<B> lookupContext,
            @NotNull ResourceKey<Registry<T>> registryKey,
            @NotNull Supplier<C> contextSupplier
    ) {
        final ContextData<T, C> contextObject = getContextObject(registryKey);
        if (lookupContext == null) return contextObject.bootstrapContext;

        final HolderGetter<T> biomeGetter = lookupContext.lookup(registryKey);
        if (biomeGetter != contextObject.lastGetter) {
            contextObject.lastGetter = biomeGetter;

            contextObject.bootstrapContext = contextSupplier.get();
            contextObject.bootstrapContext.setLookupContext(lookupContext);
            contextObject.bootstrapContext.onBootstrapContextChange(contextObject.bootstrapContext);
        } else {
            contextObject.bootstrapContext.setLookupContext(lookupContext);
        }

        return contextObject.bootstrapContext;
    }

    public static <T> void finalize(@NotNull ResourceKey<Registry<T>> registryKey) {
        getContextObject(registryKey).bootstrapContext = null;
    }
}
