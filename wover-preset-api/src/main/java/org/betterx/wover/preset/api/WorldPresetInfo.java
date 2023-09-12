package org.betterx.wover.preset.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.Nullable;

public interface WorldPresetInfo {
    int sortOrder();
    @Nullable ResourceKey<WorldPreset> overworldPreset();
    @Nullable ResourceKey<WorldPreset> netherPreset();
    @Nullable ResourceKey<WorldPreset> endPreset();

    @Nullable ResourceKey<WorldPreset> getPresetOverride(ResourceKey<LevelStem> forDimension);
    @Nullable ResourceKey<WorldPreset> getPresetOverrideRecursive(ResourceKey<LevelStem> forDimension, int maxDepth);

    default @Nullable ResourceKey<WorldPreset> getPresetOverrideRecursive(ResourceKey<LevelStem> key) {
        return getPresetOverrideRecursive(key, 10);
    }
}
