package org.betterx.wover.events.impl;

import org.betterx.wover.events.api.types.AfterCreatingNewWorld;
import org.betterx.wover.events.api.types.OnDimensionLoad;
import org.betterx.wover.events.api.types.OnFolderReady;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.types.ChainedEventImpl;
import org.betterx.wover.events.impl.types.PredicatedValueEventImpl;
import org.betterx.wover.events.impl.types.ValueEventImpl;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.LevelStorageSource;

public class WorldLifecycleImpl {
    public static final ValueEventImpl<LevelStorageSource.LevelStorageAccess, OnFolderReady> WORLD_FOLDER_READY =
            new PredicatedValueEventImpl<>(
                    "WORLD_FOLDER_READY",
                    (newStorage) -> WorldState.storageAccess() != newStorage
            );
    public static final ValueEventImpl<RegistryAccess, OnRegistryReady> WORLD_REGISTRY_READY =
            new PredicatedValueEventImpl<>(
                    "WORLD_REGISTRY_READY",
                    (newRegistry) -> WorldState.registryAccess() != newRegistry
            );
    public static final EventImpl<AfterCreatingNewWorld> CREATED_NEW_WORLD = new EventImpl<>("CREATED_NEW_WORLD");

    public static final ChainedEventImpl<LayeredRegistryAccess<RegistryLayer>, OnDimensionLoad> ON_DIMENSION_LOAD =
            new ChainedEventImpl<>("ON_DIMENSION_LOAD");
}
