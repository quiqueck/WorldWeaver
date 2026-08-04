package de.ambertation.wover.state.impl;

import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.events.api.types.OnRegistryReady;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.LevelStorageSource;

import static de.ambertation.wover.events.impl.AbstractEvent.SYSTEM_PRIORITY;

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
        WorldLifecycle.WORLD_REGISTRY_READY.subscribe(INSTANCE::setCurrentRegistryAccess, SYSTEM_PRIORITY + 1);
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(INSTANCE::setCurrentStorageAccess, SYSTEM_PRIORITY + 1);
    }

    public LevelStorageSource.LevelStorageAccess getCurrentStorageAccess() {
        return currentStorageAccess;
    }

    public void setCurrentStorageAccess(LevelStorageSource.LevelStorageAccess currentStorageAccess) {
        if (this.currentStorageAccess != currentStorageAccess)
            this.currentStorageAccess = currentStorageAccess;
    }
}
