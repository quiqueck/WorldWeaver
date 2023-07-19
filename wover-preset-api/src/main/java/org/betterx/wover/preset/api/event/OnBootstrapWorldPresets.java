package org.betterx.wover.preset.api.event;

import org.betterx.wover.events.api.Subscriber;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;

@FunctionalInterface
public interface OnBootstrapWorldPresets extends Subscriber {
    void bootstrap(WorldPresetBootstrapContext context);
}
