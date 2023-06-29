package org.betterx.wover.events.api.types;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;

@FunctionalInterface
public interface AfterCreatingNewWorld extends EventType {
    void init(
            LevelStorageSource.LevelStorageAccess storageAccess,
            RegistryAccess registryAccess,
            Holder<WorldPreset> preset,
            WorldDimensions selectedDimensions,
            boolean recreated
    );
}
