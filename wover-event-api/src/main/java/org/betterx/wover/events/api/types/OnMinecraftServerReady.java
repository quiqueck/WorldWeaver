package org.betterx.wover.events.api.types;

import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

@FunctionalInterface
public interface OnMinecraftServerReady extends EventType {
    void notify(
            LevelStorageSource.LevelStorageAccess storageSource,
            PackRepository packRepository,
            WorldStem worldStem
    );
}
