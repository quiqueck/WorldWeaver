package de.ambertation.wover.biome.api.builder.event;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.events.api.Subscriber;

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
