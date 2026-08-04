package de.ambertation.wover.events.api.types;

import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * Used for subscribers of the
 * {@link de.ambertation.wover.events.api.WorldLifecycle#BEFORE_LOADING_RESOURCES}
 * event.
 */
@FunctionalInterface
public interface BeforeLoadingResources extends Subscriber {
    /**
     * Called when the event is emitted, right before the game starts loading the
     * ({@link net.minecraft.server.ReloadableServerResources reloadable server resources}) for the world.
     *
     * @param resourceManager the resource manager that will be used to load the resources.
     * @param featureFlagSet  the feature flags that are enabled for the world.
     */
    void didLoad(ResourceManager resourceManager, FeatureFlagSet featureFlagSet);
}
