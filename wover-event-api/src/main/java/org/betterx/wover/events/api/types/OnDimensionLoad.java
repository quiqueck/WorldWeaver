package org.betterx.wover.events.api.types;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;

public interface OnDimensionLoad extends ChainableEventType<LayeredRegistryAccess<RegistryLayer>> {
}
