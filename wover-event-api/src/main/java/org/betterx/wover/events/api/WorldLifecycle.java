package org.betterx.wover.events.api;

import org.betterx.wover.events.api.types.*;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

/**
 * Provides some useful lifecycle events for the world creation/loading process.
 *
 * <p>
 * The below events get fired during world creation/loading. In General the order is:
 * <ol>
 *     <li>{@link #WORLD_FOLDER_READY}</li>
 *     <li>{@link #WORLD_REGISTRY_READY}</li>
 *     <li>{@link #CREATED_NEW_WORLD_FOLDER} (only if the world is first created)</li>
 *     <li>{@link #ON_DIMENSION_LOAD}</li>
 *     <li>{@link #MINECRAFT_SERVER_READY}</li>
 *     <li>{@link #BEFORE_CREATING_LEVELS}</li>
 * </ol>
 */
public class WorldLifecycle {
    /**
     * Gets fired when access to the world folder was first created. At this point it
     * will be possible to read data from the world folder.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link OnFolderReady}.
     */
    public static final Event<OnFolderReady> WORLD_FOLDER_READY = WorldLifecycleImpl.WORLD_FOLDER_READY;

    /**
     * Gets fired whenever a new registry access object was created. The game will create
     * multiple registry access objects during the loading/creating new worlds. In case the type of
     * registry is important to your application, you can read the
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage} value that is sent along to
     * subscribers. Only a registry flagged with
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage#FINAL} is the one that is used
     * in the actual world.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link OnRegistryReady}.
     */
    public static final Event<OnRegistryReady> WORLD_REGISTRY_READY = WorldLifecycleImpl.WORLD_REGISTRY_READY;

    /**
     * This event is fired when the game created a new folder for a new world. It is not called for
     * existing worlds (worlds that are loaded as opposed to created). You can perform any custom
     * initialization task on that folder here.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link CreatedNewWorldFolder}.
     */
    public static final Event<CreatedNewWorldFolder> CREATED_NEW_WORLD_FOLDER = WorldLifecycleImpl.CREATED_NEW_WORLD_FOLDER;

    /**
     * This event is fired when a new {@link net.minecraft.server.WorldStem} is created. It is the
     * first time the final {@link net.minecraft.core.RegistryAccess} object is available. You can use this
     * event to modify/amend the world Dimensions before they are actually loaded.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link OnDimensionLoad}. The
     * subscribers are called in sequence. Each call will recieves the DimensionsRegistry that was
     * returned by the previous subscriber. The first subscriber will recieve the original list of
     * Dimensions the game intended to load. The Final result will be used by the WorldStem.
     */
    public static final Event<OnDimensionLoad> ON_DIMENSION_LOAD = WorldLifecycleImpl.ON_DIMENSION_LOAD;

    /**
     * This event is fired when the Minecraft Server was instantiated and is ready to be used.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link OnMinecraftServerReady}.
     */
    public static final Event<OnMinecraftServerReady> MINECRAFT_SERVER_READY = WorldLifecycleImpl.MINECRAFT_SERVER_READY;

    /**
     * This event is fired before the game starts creating the levels for the world. At this point, Fabric is
     * guaranteed to have loaded all biomes, features, structures, etc. that are required for the world.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link BeforeCreatingLevels}.
     */
    public static final Event<BeforeCreatingLevels> BEFORE_CREATING_LEVELS = WorldLifecycleImpl.BEFORE_CREATING_LEVELS;

    public static final Event<OnResourceLoad> RESOURCES_LOADED = WorldLifecycleImpl.RESOURCES_LOADED;


    private WorldLifecycle() {
    }
}
