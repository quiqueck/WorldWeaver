package org.betterx.wover.events.api.types;

import net.minecraft.core.RegistryAccess;

@FunctionalInterface
public interface OnRegistryReady extends ValueEventType<RegistryAccess> {
}
