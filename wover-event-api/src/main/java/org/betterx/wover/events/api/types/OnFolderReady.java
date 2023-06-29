package org.betterx.wover.events.api.types;

import net.minecraft.world.level.storage.LevelStorageSource;

@FunctionalInterface
public interface OnFolderReady extends EventType {
    void ready(LevelStorageSource.LevelStorageAccess levelStorageAccess);
}
