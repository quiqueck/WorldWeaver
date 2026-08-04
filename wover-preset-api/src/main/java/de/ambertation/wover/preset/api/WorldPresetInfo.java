package de.ambertation.wover.preset.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.Nullable;

/**
 * Additional, WoVer-specific metadata for a {@link WorldPreset}.
 * <p>
 * A {@link WorldPreset} on its own only knows the {@link LevelStem}s it generates. This registry
 * entry adds information that is only relevant while the preset is being selected/configured, namely
 * where it should appear in the "Create World" preset list ({@link #sortOrder()}) and which other
 * preset's dimension setup should be reused for a given dimension ({@link #getPresetOverride(ResourceKey)}).
 * <p>
 * Instances are looked up from the {@link WorldPresetInfoRegistry} and are usually created with a
 * {@link WorldPresetInfoBuilder}. A preset that has no registered {@link WorldPresetInfo} falls back to a
 * default instance with the default sort order and no overrides.
 */
public interface WorldPresetInfo {
    /**
     * The position of the preset in the "Create World" preset list. Lower values are sorted first.
     *
     * @return the sort order of this preset
     */
    int sortOrder();

    /**
     * The preset whose overworld dimension should be used instead of this preset's own overworld, or
     * {@code null} if this preset defines its own overworld.
     *
     * @return the overridden preset, or {@code null}
     */
    @Nullable ResourceKey<WorldPreset> overworldPreset();

    /**
     * The preset whose nether dimension should be used instead of this preset's own nether, or
     * {@code null} if this preset defines its own nether.
     *
     * @return the overridden preset, or {@code null}
     */
    @Nullable ResourceKey<WorldPreset> netherPreset();

    /**
     * The preset whose end dimension should be used instead of this preset's own end, or
     * {@code null} if this preset defines its own end.
     *
     * @return the overridden preset, or {@code null}
     */
    @Nullable ResourceKey<WorldPreset> endPreset();

    /**
     * Gets the override for a single dimension, equivalent to calling {@link #overworldPreset()},
     * {@link #netherPreset()} or {@link #endPreset()} depending on {@code forDimension}.
     *
     * @param forDimension the dimension to get the override for
     * @return the overridden preset, or {@code null} if {@code forDimension} has no override or is not a
     *         known (overworld/nether/end) dimension key
     */
    @Nullable ResourceKey<WorldPreset> getPresetOverride(ResourceKey<LevelStem> forDimension);

    /**
     * Resolves the override for a single dimension, following the chain of overrides until a preset that
     * does not itself override {@code forDimension} is found, or {@code maxDepth} steps have been taken.
     *
     * @param forDimension the dimension to resolve the override for
     * @param maxDepth     the maximum number of overrides to follow
     * @return the resolved preset, or {@code null} if there is no override at all
     */
    @Nullable ResourceKey<WorldPreset> getPresetOverrideRecursive(ResourceKey<LevelStem> forDimension, int maxDepth);

    /**
     * Resolves the override for a single dimension, following the chain of overrides until a preset that
     * does not itself override {@code key} is found. Equivalent to calling
     * {@link #getPresetOverrideRecursive(ResourceKey, int)} with a {@code maxDepth} of {@code 10}.
     *
     * @param key the dimension to resolve the override for
     * @return the resolved preset, or {@code null} if there is no override at all
     */
    default @Nullable ResourceKey<WorldPreset> getPresetOverrideRecursive(ResourceKey<LevelStem> key) {
        return getPresetOverrideRecursive(key, 10);
    }
}
