package org.betterx.wover.preset.api.event;

import org.betterx.wover.events.api.Subscriber;
import org.betterx.wover.preset.api.context.FlatLevelPresetBootstrapContext;

@FunctionalInterface
public interface OnBootstrapFlatLevelPresets extends Subscriber {
    void bootstrap(FlatLevelPresetBootstrapContext context);
}