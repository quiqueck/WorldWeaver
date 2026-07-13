package org.betterx.wover.core.api.registry;

import net.minecraft.resources.ResourceKey;

/**
 * Callback that is notified whenever an element of a registry was (re-)loaded from a datapack. Register a watcher
 * with {@link DatapackRegistryBuilder#onElementLoad(ResourceKey, OnElementLoad)}.
 *
 * @param <T> The type of the registry-elements this watcher is notified about.
 */
public interface OnElementLoad<T> {
    /**
     * Called whenever an element of the watched registry was loaded (or reloaded) from a datapack.
     *
     * @param elementKey The key of the element that was loaded.
     * @param element    The loaded element.
     */
    void didLoadFromDatapack(ResourceKey<T> elementKey, T element);
}
