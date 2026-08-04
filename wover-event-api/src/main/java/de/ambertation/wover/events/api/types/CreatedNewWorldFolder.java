package de.ambertation.wover.events.api.types;

import de.ambertation.wover.events.api.Subscriber;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;

/**
 * Used for subscribers of the
 * {@link de.ambertation.wover.events.api.WorldLifecycle#CREATED_NEW_WORLD_FOLDER}
 * event.
 */
@FunctionalInterface
public interface CreatedNewWorldFolder extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param storageAccess      the storage access that was used to create
     *                           the world folder.
     * @param registryAccess     the registry access that was used to create
     *                           the world folder.
     * @param preset             the preset that was used to create the world.
     * @param selectedDimensions the dimensions that were selected for the world.
     * @param recreated          whether the world was recreated or not.
     */
    void init(
            LevelStorageSource.LevelStorageAccess storageAccess,
            HolderLookup.Provider registryAccess,
            Holder<WorldPreset> preset,
            WorldDimensions selectedDimensions,
            boolean recreated
    );
}
