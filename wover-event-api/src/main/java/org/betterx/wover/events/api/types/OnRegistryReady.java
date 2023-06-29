package org.betterx.wover.events.api.types;

import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface OnRegistryReady extends EventType {
    enum Stage {PREPARATION, LOADING, FINAL}
    void ready(RegistryAccess registryAccess, Stage stage);
}
