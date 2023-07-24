package org.betterx.wover.preset.api;

import org.betterx.wover.preset.impl.SortableWorldPresetImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;

/**
 * A {@link WorldPreset} that can be sorted and provides access to otherwise
 * private data structures
 */
public abstract class SortableWorldPreset extends WorldPreset {
    protected SortableWorldPreset(Map<ResourceKey<LevelStem>, LevelStem> map) {
        super(map);
    }

    public abstract WorldDimensions getWorldDimensions();
    public abstract LevelStem getDimension(ResourceKey<LevelStem> key);

    public static SortableWorldPreset fromStems(
            LevelStem overworldStem,
            LevelStem netherStem,
            LevelStem endStem
    ) {
        return SortableWorldPresetImpl.fromStems(overworldStem, netherStem, endStem);
    }
}
