package de.ambertation.wover.preset.api.event;

import de.ambertation.wover.events.api.Subscriber;
import de.ambertation.wover.preset.api.context.FlatLevelPresetBootstrapContext;

/**
 * Used by
 * {@link de.ambertation.wover.preset.api.flat.FlatLevelPresetManager#BOOTSTRAP_FLAT_LEVEL_PRESETS}
 */
@FunctionalInterface
public interface OnBootstrapFlatLevelPresets extends Subscriber {
    /**
     * Called when the registry is being bootstrapped.
     *
     * @param context The bootstrap context.
     */
    void bootstrap(FlatLevelPresetBootstrapContext context);
}