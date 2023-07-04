package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.data.worldgen.BootstapContext;

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
    void bootstrap(BootstapContext<T> context);
}
