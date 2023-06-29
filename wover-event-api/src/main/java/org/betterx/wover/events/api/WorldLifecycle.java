package org.betterx.wover.events.api;

import org.betterx.wover.events.api.types.*;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

public class WorldLifecycle {
    public static final Event<OnFolderReady> WORLD_FOLDER_READY = WorldLifecycleImpl.WORLD_FOLDER_READY;
    public static final Event<OnRegistryReady> WORLD_REGISTRY_READY = WorldLifecycleImpl.WORLD_REGISTRY_READY;
    public static final Event<AfterCreatingNewWorld> CREATED_NEW_WORLD_FOLDER = WorldLifecycleImpl.CREATED_NEW_WORLD_FOLDER;
    public static final Event<OnDimensionLoad> ON_DIMENSION_LOAD = WorldLifecycleImpl.ON_DIMENSION_LOAD;
    public static final Event<OnMinecraftServerReady> MINECRAFT_SERVER_READY = WorldLifecycleImpl.MINECRAFT_SERVER_READY;
    public static final Event<BeforeCreatingLevels> BEFORE_CREATING_LEVELS = WorldLifecycleImpl.BEFORE_CREATING_LEVELS;
}
