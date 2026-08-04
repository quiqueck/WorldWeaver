package de.ambertation.wover.preset.api.event;

import de.ambertation.wover.events.api.Subscriber;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;

/**
 * Used by
 * {@link de.ambertation.wover.preset.api.WorldPresetManager#BOOTSTRAP_WORLD_PRESETS}
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
