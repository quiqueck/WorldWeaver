package org.betterx.wover.preset.api.event;

import org.betterx.wover.events.api.Subscriber;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;

/**
 * Used by
 * {@link org.betterx.wover.preset.api.WorldPresetManager#BOOTSTRAP_WORLD_PRESETS}
 */
@FunctionalInterface
public interface OnBootstrapWorldPresets extends Subscriber {
    /**
     * Called when the registry is being bootstrapped.
     *
     * @param context The bootstrap context.
     */
    void bootstrap(WorldPresetBootstrapContext context);
}
