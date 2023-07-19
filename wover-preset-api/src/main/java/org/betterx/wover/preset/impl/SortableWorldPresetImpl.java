package org.betterx.wover.preset.impl;

import org.betterx.wover.preset.api.SortableWorldPreset;
import org.betterx.wover.preset.mixin.WorldPresetAccessor;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class SortableWorldPresetImpl extends SortableWorldPreset {
    private static int NEXT_IN_SORT_ORDER = 1000;
    public final int sortOrder;
    @Nullable
    public final ResourceKey<WorldPreset> parentKey;
    private final WorldDimensions worldDimensions;

    public SortableWorldPresetImpl(
            Map<ResourceKey<LevelStem>, LevelStem> map,
            Optional<Integer> sortOrder
    ) {
        this(map, sortOrder.orElse(NEXT_IN_SORT_ORDER++), null);
    }

    public SortableWorldPresetImpl(
            Map<ResourceKey<LevelStem>, LevelStem> map,
            Optional<Integer> sortOrder,
            @Nullable ResourceKey<WorldPreset> parentKey
    ) {
        this(map, sortOrder.orElse(NEXT_IN_SORT_ORDER++), parentKey);
    }

    public SortableWorldPresetImpl(
            Map<ResourceKey<LevelStem>, LevelStem> map,
            int sortOrder
    ) {
        this(map, sortOrder, null);
    }

    public SortableWorldPresetImpl(
            Map<ResourceKey<LevelStem>, LevelStem> map,
            int sortOrder,
            @Nullable ResourceKey<WorldPreset> parentKey
    ) {
        super(map);
        this.sortOrder = sortOrder;
        this.worldDimensions = buildWorldDimensions(map);
        this.parentKey = parentKey;
    }

    public WorldDimensions getWorldDimensions() {
        return this.worldDimensions;
    }


    private Map<ResourceKey<LevelStem>, LevelStem> getDimensions() {
        return ((WorldPresetAccessor) this).wover_getDimensions();
    }

    public LevelStem getDimension(ResourceKey<LevelStem> key) {
        return getDimensions().get(key);
    }

    public SortableWorldPresetImpl withDimensions(
            Registry<LevelStem> dimensions,
            @Nullable ResourceKey<WorldPreset> parentKey
    ) {
        Map<ResourceKey<LevelStem>, LevelStem> map = new HashMap<>();
        for (var entry : dimensions.entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();
            map.put(key, stem);
        }
        return new SortableWorldPresetImpl(map, sortOrder, parentKey);
    }


    public static WorldDimensions buildWorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> map) {
        Registry<LevelStem> registry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.experimental());
        for (var entry : map.entrySet()) {
            Registry.register(registry, entry.getKey(), entry.getValue());
        }

        return new WorldDimensions(registry);
    }


    public static SortableWorldPresetImpl fromStems(
            LevelStem overworldStem,
            LevelStem netherStem,
            LevelStem endStem
    ) {
        final Map<ResourceKey<LevelStem>, LevelStem> map = Map.of(
                LevelStem.OVERWORLD, overworldStem,
                LevelStem.NETHER, netherStem,
                LevelStem.END, endStem
        );
        return new SortableWorldPresetImpl(map, Optional.empty());
    }
}