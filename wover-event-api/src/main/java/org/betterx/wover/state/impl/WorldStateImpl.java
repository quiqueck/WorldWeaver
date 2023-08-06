package org.betterx.wover.state.impl;

import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnRegistryReady;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.LevelStorageSource;

public final class WorldStateImpl {
    public static final WorldStateImpl INSTANCE = new WorldStateImpl();
    private RegistryAccess currentRegistryAccess;
    public RegistryAccess currentAllStageRegistryAccess;
    private LevelStorageSource.LevelStorageAccess currentStorageAccess;

    private WorldStateImpl() {
    }

    public RegistryAccess getCurrentRegistryAccess() {
        return currentRegistryAccess;
    }

    public void setCurrentRegistryAccess(RegistryAccess newRegistry, OnRegistryReady.Stage stage) {
        currentAllStageRegistryAccess = newRegistry;

        if (stage == OnRegistryReady.Stage.PREPARATION) return;

        if (this.currentRegistryAccess != newRegistry) {
            this.currentRegistryAccess = newRegistry;
        }
    }

    public static void ensureStaticallyLoaded() {
        assert INSTANCE != null;
        WorldLifecycle.WORLD_REGISTRY_READY.subscribe(INSTANCE::setCurrentRegistryAccess);
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(INSTANCE::setCurrentStorageAccess);
    }

    public LevelStorageSource.LevelStorageAccess getCurrentStorageAccess() {
        return currentStorageAccess;
    }

    public void setCurrentStorageAccess(LevelStorageSource.LevelStorageAccess currentStorageAccess) {
        if (this.currentStorageAccess != currentStorageAccess)
            this.currentStorageAccess = currentStorageAccess;
    }
}
