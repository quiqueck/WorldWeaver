package de.ambertation.wover.events.api.types;

import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.data.worldgen.BootstrapContext;

/**
 * Used by registry bootstrap events.
 *
 * @param <T> The content type of the registry.
 */
public interface OnBootstrapRegistry<T> extends Subscriber {
    /**
     * Called when the registry is being bootstrapped.
     *
     * @param context The bootstrap context.
     */
    void bootstrap(BootstrapContext<T> context);
}
