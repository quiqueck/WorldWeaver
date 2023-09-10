package org.betterx.wover.preset.api;

import org.betterx.wover.preset.impl.WorldPresetInfoBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public interface WorldPresetInfoBuilder {
    WorldPresetInfoBuilder order(int order);

    WorldPresetInfoBuilder overworldOverride(ResourceKey<WorldPreset> overworldLike);

    WorldPresetInfoBuilder netherOverride(ResourceKey<WorldPreset> netherLike);

    WorldPresetInfoBuilder endOverride(ResourceKey<WorldPreset> endLike);

    WorldPresetInfo build();

    Holder<WorldPresetInfo> register(ResourceKey<WorldPreset> key);

    static WorldPresetInfoBuilder start(BootstapContext<WorldPresetInfo> context) {
        return new WorldPresetInfoBuilderImpl(context);
    }
}
