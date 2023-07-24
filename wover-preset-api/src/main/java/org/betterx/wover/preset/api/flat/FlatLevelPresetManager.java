package org.betterx.wover.preset.api.flat;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapFlatLevelPresets;
import org.betterx.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class FlatLevelPresetManager {
    public static final Event<OnBootstrapFlatLevelPresets> BOOTSTRAP_FLAT_LEVEL_PRESETS = FlatLevelPresetManagerImpl.BOOTSTRAP_FLAT_LEVEL_PRESETS;

    public static ResourceKey<FlatLevelGeneratorPreset> createKey(ResourceLocation loc) {
        return FlatLevelPresetManagerImpl.createKey(loc);
    }
}
