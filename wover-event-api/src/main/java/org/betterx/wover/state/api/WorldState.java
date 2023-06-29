package org.betterx.wover.state.api;

import org.betterx.wover.state.impl.WorldStateImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * A collection of useful methods to access the current world state.
 * <p>
 * The state is fed by some events, so it is not guaranteed to be available at all times.
 */
public class WorldState {
    /**
     * The current registry access for the world.It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY} event.
     * Only Registries that are not sent with the {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage}
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage#PREPARATION}
     * are stored.
     *
     * @return the current registry access for the world.
     */
    public static RegistryAccess registryAccess() {
        return WorldStateImpl.INSTANCE.getCurrentRegistryAccess();
    }

    /**
     * The current storage access for the world.It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_FOLDER_READY} event.
     *
     * @return the current storage access for the world.
     */
    public static LevelStorageSource.LevelStorageAccess storageAccess() {
        return WorldStateImpl.INSTANCE.getCurrentStorageAccess();
    }
}
