package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#BEFORE_CREATING_LEVELS}
 * event.
 */
@FunctionalInterface
public interface BeforeCreatingLevels extends Subscriber {

    /**
     * Called when the event is emitted.
     *
     * @param storageAccess  the storage access that was used to open
     *                       the world folder.
     * @param packRepository the pack repository of the world
     * @param registries     the registries of the world
     * @param worldData      the general data of the world
     */
    void notify(
            LevelStorageSource.LevelStorageAccess storageAccess,
            PackRepository packRepository,
            LayeredRegistryAccess<RegistryLayer> registries,
            WorldData worldData
    );
}
