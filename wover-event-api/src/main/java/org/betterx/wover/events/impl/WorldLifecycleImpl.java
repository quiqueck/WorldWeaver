package org.betterx.wover.events.impl;

import org.betterx.wover.events.api.types.*;
import org.betterx.wover.events.impl.types.ChainedEventImpl;
import org.betterx.wover.events.impl.types.FolderReadyEventImpl;
import org.betterx.wover.events.impl.types.RegistryReadyEventImpl;
import org.betterx.wover.events.impl.types.ResourcesReadyEventImpl;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;

public class WorldLifecycleImpl {
    public static final EventImpl<CreatedNewWorldFolder> CREATED_NEW_WORLD_FOLDER = new EventImpl<>(
            "CREATED_NEW_WORLD_FOLDER");

    public static final FolderReadyEventImpl WORLD_FOLDER_READY =
            new FolderReadyEventImpl("WORLD_FOLDER_READY");

    public static final RegistryReadyEventImpl WORLD_REGISTRY_READY =
            new RegistryReadyEventImpl("WORLD_REGISTRY_READY");


    public static final EventImpl<BeforeLoadingResources> BEFORE_LOADING_RESOURCES =
            new EventImpl<>("BEFORE_LOADING_RESOURCES");

    public static final ChainedEventImpl<LayeredRegistryAccess<RegistryLayer>, OnDimensionLoad> ON_DIMENSION_LOAD =
            new ChainedEventImpl<>("ON_DIMENSION_LOAD");

    public static final EventImpl<BeforeCreatingLevels> BEFORE_CREATING_LEVELS =
            new EventImpl<>("BEFORE_CREATING_LEVELS");
    public static final EventImpl<OnMinecraftServerReady> MINECRAFT_SERVER_READY =
            new EventImpl<>("MINECRAFT_SERVER_READY");
    public static final EventImpl<OnServerLevelReady> SERVER_LEVEL_READY =
            new EventImpl<>("SERVER_LEVEL_READY");

    //Internally uses BEFORE_LOADING_RESOURCES, so it needs to be initialized after that
    public static final ResourcesReadyEventImpl RESOURCES_LOADED =
            new ResourcesReadyEventImpl("RESOURCES_LOADED");
}
