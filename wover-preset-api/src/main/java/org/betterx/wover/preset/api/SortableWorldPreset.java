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
    /**
     * Creates a new {@link SortableWorldPreset} from a map of {@link LevelStem}s.
     *
     * @param map The level stems.
     */
    protected SortableWorldPreset(Map<ResourceKey<LevelStem>, LevelStem> map) {
        super(map);
    }

    /**
     * Gets the {@link WorldDimensions} of this preset.
     *
     * @return The dimensions.
     */
    public abstract WorldDimensions getWorldDimensions();
    /**
     * Gets the {@link LevelStem} for the given {@link ResourceKey}.
     *
     * @param key The stems key
     * @return The stem.
     */
    public abstract LevelStem getDimension(ResourceKey<LevelStem> key);

    /**
     * Creates a new {@link SortableWorldPreset} from typical stems.
     *
     * @param overworldStem The overworld stem.
     * @param netherStem    The nether stem.
     * @param endStem       The end stem.
     * @return The {@link WorldPreset}.
     */
    public static SortableWorldPreset fromStems(
            LevelStem overworldStem,
            LevelStem netherStem,
            LevelStem endStem
    ) {
        return SortableWorldPresetImpl.fromStems(overworldStem, netherStem, endStem);
    }

    /**
     * Creates a new {@link SortableWorldPreset} from a map of {@link LevelStem}s.
     *
     * @param dimensions The stems.
     * @param sortOrder  The sorting order.
     * @return The {@link SortableWorldPreset}.
     */
    public static SortableWorldPreset of(Map<ResourceKey<LevelStem>, LevelStem> dimensions, int sortOrder) {
        return new SortableWorldPresetImpl(dimensions, sortOrder);
    }
}
