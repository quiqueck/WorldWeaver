package org.betterx.wover.preset.api;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapWorldPresets;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresetManager {
    public static final Event<OnBootstrapWorldPresets> BOOTSTRAP_WORLD_PRESETS = WorldPresetsManagerImpl.BOOTSTRAP_WORLD_PRESETS;

    public static ResourceKey<WorldPreset> createKey(ResourceLocation loc) {
        return WorldPresetsManagerImpl.createKey(loc);
    }

    public static Holder<WorldPreset> get(RegistryAccess access, ResourceKey<WorldPreset> key) {
        return WorldPresetsManagerImpl.get(access, key);
    }

    public static void suggestDefault(ResourceKey<WorldPreset> preset, int priority) {
        WorldPresetsManagerImpl.suggestDefault(preset, priority);
    }
}
