package org.betterx.wover.events.api.types;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

@FunctionalInterface
public interface BeforeCreatingLevels extends EventType {
    void notify(
            LevelStorageSource.LevelStorageAccess storageSource,
            PackRepository packRepository,
            LayeredRegistryAccess<RegistryLayer> registries,
            WorldData worldData
    );
}
