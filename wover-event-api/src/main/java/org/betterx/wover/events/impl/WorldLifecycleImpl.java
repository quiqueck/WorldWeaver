package org.betterx.wover.events.impl;

import org.betterx.wover.events.api.types.*;
import org.betterx.wover.events.impl.types.ChainedEventImpl;
import org.betterx.wover.events.impl.types.FolderReadyEventImpl;
import org.betterx.wover.events.impl.types.RegistryReadyEventImpl;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;

public class WorldLifecycleImpl {
    public static final EventImpl<CreatedNewWorldFolder> CREATED_NEW_WORLD_FOLDER = new EventImpl<>(
            "CREATED_NEW_WORLD_FOLDER");

    public static final FolderReadyEventImpl WORLD_FOLDER_READY =
            new FolderReadyEventImpl("WORLD_FOLDER_READY");

    public static final RegistryReadyEventImpl WORLD_REGISTRY_READY =
            new RegistryReadyEventImpl("WORLD_REGISTRY_READY");

    public static final ChainedEventImpl<LayeredRegistryAccess<RegistryLayer>, OnDimensionLoad> ON_DIMENSION_LOAD =
            new ChainedEventImpl<>("ON_DIMENSION_LOAD");

    public static final EventImpl<BeforeCreatingLevels> BEFORE_CREATING_LEVELS =
            new EventImpl<>("BEFORE_CREATING_LEVELS");
    public static final EventImpl<OnMinecraftServerReady> MINECRAFT_SERVER_READY =
            new EventImpl<>("MINECRAFT_SERVER_READY");

    public static final EventImpl<OnResourceLoad> RESOURCES_LOADED =
            new EventImpl<>("RESOURCES_LOADED");
}
