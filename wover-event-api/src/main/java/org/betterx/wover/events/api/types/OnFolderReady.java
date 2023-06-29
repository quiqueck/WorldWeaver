package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_FOLDER_READY}
 * event.
 */
@FunctionalInterface
public interface OnFolderReady extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param levelStorageAccess the storage access that was used to open
     *                           the world folder.
     */
    void ready(LevelStorageSource.LevelStorageAccess levelStorageAccess);
}
