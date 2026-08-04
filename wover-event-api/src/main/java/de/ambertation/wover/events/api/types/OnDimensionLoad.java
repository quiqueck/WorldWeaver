package de.ambertation.wover.events.api.types;

import de.ambertation.wover.events.api.ChainableSubscriber;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;

/**
 * Used for subscribers of the
 * {@link de.ambertation.wover.events.api.WorldLifecycle#ON_DIMENSION_LOAD}
 * event.
 */
@FunctionalInterface
public interface OnDimensionLoad extends ChainableSubscriber<LayeredRegistryAccess<RegistryLayer>> {
}
