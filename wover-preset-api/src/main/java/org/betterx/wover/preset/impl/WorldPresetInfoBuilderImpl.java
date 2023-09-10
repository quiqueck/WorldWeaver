package org.betterx.wover.preset.impl;

import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.api.WorldPresetInfoBuilder;
import org.betterx.wover.util.PriorityLinkedList;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class WorldPresetInfoBuilderImpl implements WorldPresetInfoBuilder {
    private int sortOrder;
    private @Nullable
    ResourceKey<WorldPreset> overworldLike;
    private @Nullable
    ResourceKey<WorldPreset> netherLike;
    private @Nullable
    ResourceKey<WorldPreset> endLike;

    private final BootstapContext<WorldPresetInfo> context;

    @ApiStatus.Internal
    public WorldPresetInfoBuilderImpl(BootstapContext<WorldPresetInfo> context) {
        sortOrder = PriorityLinkedList.DEFAULT_PRIORITY;
        this.context = context;
    }

    public WorldPresetInfoBuilder order(int order) {
        sortOrder = order;
        return this;
    }

    public WorldPresetInfoBuilder overworldOverride(ResourceKey<WorldPreset> overworldLike) {
        this.overworldLike = overworldLike;
        return this;
    }

    public WorldPresetInfoBuilder netherOverride(ResourceKey<WorldPreset> netherLike) {
        this.netherLike = netherLike;
        return this;
    }

    public WorldPresetInfoBuilder endOverride(ResourceKey<WorldPreset> endLike) {
        this.endLike = endLike;
        return this;
    }

    public WorldPresetInfo build() {
        return new WorldPresetInfoImpl(
                sortOrder,
                overworldLike, netherLike, endLike
        );
    }

    @Override
    public Holder<WorldPresetInfo> register(ResourceKey<WorldPreset> key) {
        return WorldPresetInfoRegistryImpl.register(context, key, build());
    }
}
