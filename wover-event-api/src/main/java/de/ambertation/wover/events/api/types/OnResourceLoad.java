package de.ambertation.wover.events.api.types;

import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Used for subscribers of the
 * {@link de.ambertation.wover.events.api.WorldLifecycle#RESOURCES_LOADED}
 * event.
 */
@FunctionalInterface
public interface OnResourceLoad extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param resourceManager the resource manager that was used to load
     */
    void bootstrap(ResourceManager resourceManager);
}
