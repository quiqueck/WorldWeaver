package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.ChainableSubscriber;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#ON_DIMENSION_LOAD}
 * event.
 */
@FunctionalInterface
public interface OnDimensionLoad extends ChainableSubscriber<LayeredRegistryAccess<RegistryLayer>> {
}
