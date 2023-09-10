package org.betterx.wover.preset.api;

import org.betterx.wover.preset.impl.WorldPresetInfoImpl;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface WorldPresetInfo {
    int sortOrder();
    @Nullable ResourceKey<WorldPreset> overworldPreset();
    @Nullable ResourceKey<WorldPreset> netherPreset();
    @Nullable ResourceKey<WorldPreset> endPreset();

    @Nullable ResourceKey<WorldPreset> getPresetOverride(ResourceKey<LevelStem> key);

    ResourceKey<WorldPreset> parentKey();

    public static Map<ResourceKey<LevelStem>, LevelStem> getDimensions(Holder<WorldPreset> preset) {
        return WorldPresetInfoImpl.getDimensions(preset);
    }

    /**
     * Gets the {@link LevelStem} for the given {@link ResourceKey}.
     *
     * @param preset The preset to read
     * @param key    The stems key
     * @return The stem.
     */
    static LevelStem getDimension(Holder<WorldPreset> preset, ResourceKey<LevelStem> key) {
        return WorldPresetInfoImpl.getDimension(preset, key);
    }
}
