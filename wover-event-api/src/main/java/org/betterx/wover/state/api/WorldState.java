package org.betterx.wover.state.api;

import org.betterx.wover.state.impl.WorldStateImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.LevelStorageSource;

public class WorldState {
    public static RegistryAccess registryAccess() {
        return WorldStateImpl.INSTANCE.getCurrentRegistryAccess();
    }

    public static LevelStorageSource.LevelStorageAccess storageAccess() {
        return WorldStateImpl.INSTANCE.getCurrentStorageAccess();
    }
}
