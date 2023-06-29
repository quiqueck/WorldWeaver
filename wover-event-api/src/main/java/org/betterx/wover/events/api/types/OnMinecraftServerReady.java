package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#MINECRAFT_SERVER_READY}
 * event.
 */
@FunctionalInterface
public interface OnMinecraftServerReady extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param storageSource  the storage source that was used to open
     *                       the world folder.
     * @param packRepository the pack repository of the world
     * @param worldStem      the stem of the world. The stem provides access to
     *                       the world registry, dimension registry, and other
     *                       values used during setup.
     */
    void notify(
            LevelStorageSource.LevelStorageAccess storageSource,
            PackRepository packRepository,
            WorldStem worldStem
    );
}
