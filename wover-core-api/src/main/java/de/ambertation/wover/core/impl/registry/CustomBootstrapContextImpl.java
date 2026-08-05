package de.ambertation.wover.core.impl.registry;

import de.ambertation.wover.core.api.registry.CustomBootstrapContext;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CustomBootstrapContextImpl<T> {
    private static class ContextData<T, C extends CustomBootstrapContext<T, C>> {
        private HolderGetter<T> lastGetter = null;
        private C bootstrapContext = null;

    }

    // 26.1 loads worldgen registries in parallel: distinct registries (biome_data, biome_modifications,
    // surface_rules, ...) bootstrap on different worker threads, so this map is touched concurrently.
    // ConcurrentHashMap makes computeIfAbsent thread-safe; per-ContextData mutations are guarded by a
    // synchronized(contextObject) block below.
    private static final Map<ResourceKey<Registry<?>>, ContextData<?, ?>> CONTEXT_OBJECTS = new ConcurrentHashMap<>();


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
            @Nullable BootstrapContext<B> lookupContext,
            @NotNull ResourceKey<Registry<T>> registryKey,
            @NotNull Supplier<C> contextSupplier
    ) {
        final ContextData<T, C> contextObject = getContextObject(registryKey);
        // The check-then-set on lastGetter/bootstrapContext must be atomic per registry: under 26.1's
        // parallel registry loading the same registry can be initialized from more than one thread.
        synchronized (contextObject) {
            if (lookupContext == null) return contextObject.bootstrapContext;

            final HolderGetter<T> biomeGetter = lookupContext.lookup(registryKey);
            if (biomeGetter != contextObject.lastGetter || contextObject.bootstrapContext == null) {
                contextObject.lastGetter = biomeGetter;

                contextObject.bootstrapContext = contextSupplier.get();
                contextObject.bootstrapContext.setLookupContext(lookupContext);
                contextObject.bootstrapContext.onBootstrapContextChange(contextObject.bootstrapContext);
            } else {
                contextObject.bootstrapContext.setLookupContext(lookupContext);
            }

            return contextObject.bootstrapContext;
        }
    }

    public static <T> void finalize(@NotNull ResourceKey<Registry<T>> registryKey) {
        final ContextData<?, ?> contextObject = getContextObject(registryKey);
        synchronized (contextObject) {
            contextObject.bootstrapContext = null;
        }
    }
}
