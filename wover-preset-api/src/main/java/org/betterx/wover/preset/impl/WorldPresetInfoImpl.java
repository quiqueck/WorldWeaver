package org.betterx.wover.preset.impl;

import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.api.WorldPresetInfoRegistry;
import org.betterx.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

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
            null, null, null
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
                end.orElse(null)
        );
    }

    private final int sortOrder;
    private @Nullable
    final ResourceKey<WorldPreset> overworldLike;
    private @Nullable
    final ResourceKey<WorldPreset> netherLike;
    private @Nullable
    final ResourceKey<WorldPreset> endLike;

    public WorldPresetInfoImpl(int order) {
        this(order, null, null, null);
    }


    WorldPresetInfoImpl(
            int sortOrder,
            @Nullable ResourceKey<WorldPreset> overworldLike,
            @Nullable ResourceKey<WorldPreset> netherLike,
            @Nullable ResourceKey<WorldPreset> endLike
    ) {
        this.sortOrder = sortOrder;
        this.overworldLike = overworldLike;
        this.netherLike = netherLike;
        this.endLike = endLike;
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

    @Override
    public @Nullable ResourceKey<WorldPreset> getPresetOverrideRecursive(
            ResourceKey<LevelStem> forDimension,
            int count
    ) {
        //see if the preset has an override for this dimension, if so find the real preset to use
        ResourceKey<WorldPreset> configuredKey = null;
        ResourceKey<WorldPreset> overrideKey = this.getPresetOverride(forDimension);
        
        while (overrideKey != null && count > 0) {
            configuredKey = overrideKey;
            overrideKey = WorldPresetInfoRegistry.getFor(configuredKey).getPresetOverride(forDimension);
            count--;
        }

        return configuredKey;
    }
}
