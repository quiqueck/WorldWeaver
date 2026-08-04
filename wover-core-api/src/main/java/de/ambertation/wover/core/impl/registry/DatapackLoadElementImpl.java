package de.ambertation.wover.core.impl.registry;

import de.ambertation.wover.core.api.registry.OnElementLoad;
import de.ambertation.wover.util.PriorityLinkedList;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class DatapackLoadElementImpl {
    public static final int DEFAULT_PRIORITY = DatapackRegistryBuilderImpl.DEFAULT_PRIORITY;
    public static final int MAX_READONLY_PRIORITY = DatapackRegistryBuilderImpl.MAX_READONLY_PRIORITY;
    private static final Map<ResourceKey<Registry<?>>, PriorityLinkedList<OnElementLoad>> WATCHERS = new HashMap<>();


    public static <E> boolean isRegistered(ResourceKey<Registry<E>> registryId) {
        return WATCHERS.containsKey(registryId);
    }

    public static <E, R extends Registry<E>> PriorityLinkedList<OnElementLoad> getWatchers(ResourceKey<R> registryId) {
        ResourceKey<Registry<?>> rKey = (ResourceKey<Registry<?>>) registryId;
        return WATCHERS.computeIfAbsent(
                rKey,
                k -> new PriorityLinkedList<>()
        );
    }

    public static <E> void register(
            ResourceKey<Registry<E>> registryKey,
            OnElementLoad<E> watcher
    ) {
        getWatchers(registryKey).add(watcher, DEFAULT_PRIORITY);
    }

    public static <E> void register(
            ResourceKey<Registry<E>> registryKey,
            OnElementLoad<E> watcher,
            int priority
    ) {
        getWatchers(registryKey).add(watcher, Math.max(MAX_READONLY_PRIORITY + 1, priority));
    }

    @ApiStatus.Internal
    public static <E> void didLoadFromDatapack(
            ResourceKey<E> elementKey,
            E element
    ) {
        if (isRegistered(elementKey.registryKey())) {
            getWatchers(elementKey.registryKey())
                    .forEach(watcher -> watcher.didLoadFromDatapack(elementKey, element));
        }
    }
}
