package org.betterx.wover.events.api;

import org.betterx.wover.events.api.types.AfterCreatingNewWorld;
import org.betterx.wover.events.api.types.OnDimensionLoad;
import org.betterx.wover.events.api.types.OnFolderReady;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

public class WorldLifecycle {
    public static final Event<OnFolderReady> WORLD_FOLDER_READY = WorldLifecycleImpl.WORLD_FOLDER_READY;
    public static final Event<OnRegistryReady> WORLD_REGISTRY_READY = WorldLifecycleImpl.WORLD_REGISTRY_READY;
    public static final Event<AfterCreatingNewWorld> CREATED_NEW_WORLD = WorldLifecycleImpl.CREATED_NEW_WORLD;
    public static final Event<OnDimensionLoad> ON_DIMENSION_LOAD = WorldLifecycleImpl.ON_DIMENSION_LOAD;
}
