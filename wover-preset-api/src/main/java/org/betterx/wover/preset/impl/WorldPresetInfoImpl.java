package org.betterx.wover.preset.impl;

import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.mixin.WorldPresetAccessor;
import org.betterx.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class WorldPresetInfoImpl implements WorldPresetInfo {
    public static final Codec<WorldPresetInfo> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.INT.optionalFieldOf("sort_order", PriorityLinkedList.DEFAULT_PRIORITY)
                             .forGetter(o -> o.sortOrder()),
                    ResourceKey.codec(Registries.WORLD_PRESET)
                               .optionalFieldOf("overworld_preset")
                               .forGetter(o -> Optional.ofNullable(o.overworldPreset())),
                    ResourceKey.codec(Registries.WORLD_PRESET)
                               .optionalFieldOf("nether_preset")
                               .forGetter(o -> Optional.ofNullable(o.netherPreset())),
                    ResourceKey.codec(Registries.WORLD_PRESET)
                               .optionalFieldOf("end_preset")
                               .forGetter(o -> Optional.ofNullable(o.endPreset()))
            )
            .apply(instance, WorldPresetInfoImpl::fromOptionals)
    );

    public static final WorldPresetInfo DEFAULT = new WorldPresetInfoImpl(
            PriorityLinkedList.DEFAULT_PRIORITY,
            null,
            null,
            null,
            null
    );

    private static WorldPresetInfo fromOptionals(
            Integer sort,
            Optional<ResourceKey<WorldPreset>> overworld,
            Optional<ResourceKey<WorldPreset>> nether,
            Optional<ResourceKey<WorldPreset>> end
    ) {
        return new WorldPresetInfoImpl(
                sort,
                overworld.orElse(null),
                nether.orElse(null),
                end.orElse(null),
                null
        );
    }

    private final int sortOrder;
    private @Nullable
    final ResourceKey<WorldPreset> overworldLike;
    private @Nullable
    final ResourceKey<WorldPreset> netherLike;
    private @Nullable
    final ResourceKey<WorldPreset> endLike;

    @Nullable
    public final ResourceKey<WorldPreset> parentKey;

    public WorldPresetInfoImpl(int order) {
        this(order, null, null, null, null);
    }


    WorldPresetInfoImpl(
            int sortOrder,
            @Nullable ResourceKey<WorldPreset> overworldLike,
            @Nullable ResourceKey<WorldPreset> netherLike,
            @Nullable ResourceKey<WorldPreset> endLike,
            @Nullable ResourceKey<WorldPreset> parentKey
    ) {
        this.sortOrder = sortOrder;
        this.overworldLike = overworldLike;
        this.netherLike = netherLike;
        this.endLike = endLike;
        this.parentKey = parentKey;
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

    public ResourceKey<WorldPreset> parentKey() {
        return parentKey;
    }

    public int sortOrder() {
        return this.sortOrder;
    }

    public @Nullable ResourceKey<WorldPreset> overworldPreset() {
        return this.overworldLike;
    }

    public @Nullable ResourceKey<WorldPreset> netherPreset() {
        return this.netherLike;
    }

    public @Nullable ResourceKey<WorldPreset> endPreset() {
        return this.endLike;
    }

    @Override
    public @Nullable ResourceKey<WorldPreset> getPresetOverride(ResourceKey<LevelStem> key) {
        if (key.location().equals(LevelStem.OVERWORLD.location()))
            return overworldPreset();
        if (key.location().equals(LevelStem.NETHER.location()))
            return netherPreset();
        if (key.location().equals(LevelStem.END.location()))
            return endPreset();
        return null;
    }

    public static Map<ResourceKey<LevelStem>, LevelStem> getDimensions(Holder<WorldPreset> preset) {
        if (preset.value() instanceof WorldPresetAccessor acc)
            return acc.wover_getDimensions();

        return Map.of();
    }

    public static LevelStem getDimension(Holder<WorldPreset> preset, ResourceKey<LevelStem> key) {
        return getDimensions(preset).get(key);
    }
}
