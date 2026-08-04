package de.ambertation.wover.events.impl.types;

import de.ambertation.wover.events.api.types.OnResourceLoad;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;

public class ResourcesReadyEventImpl extends EventImpl<OnResourceLoad> {
    private ResourceManager lastResourceManager;

    public ResourcesReadyEventImpl(String eventName) {
        super(eventName);
        WorldLifecycleImpl.BEFORE_LOADING_RESOURCES.subscribe(this::reset, SYSTEM_PRIORITY);
    }

    private void reset(ResourceManager resourceManager, FeatureFlagSet featureFlagSet) {
        this.lastResourceManager = null;
    }

    public void emit(ResourceManager resourceManager) {
        if (resourceManager != lastResourceManager) {
            lastResourceManager = resourceManager;
            super.emit(c -> c.bootstrap(resourceManager));
        }
    }
}
