package org.betterx.wover.biome.api.builder.event;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.events.api.Subscriber;

/**
 * Used by the Biome-Registry bootstrap event.
 */
public interface OnBootstrapBiomes extends Subscriber {
    /**
     * Called when the registry is being bootstrapped.
     *
     * @param context The bootstrap context.
     */
    void bootstrap(BiomeBootstrapContext context);
}
