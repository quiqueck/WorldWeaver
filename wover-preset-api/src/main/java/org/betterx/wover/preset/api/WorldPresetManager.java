package org.betterx.wover.preset.api;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapWorldPresets;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

/**
 * Utility class for {@link WorldPreset}s.
 * <p>
 * The class provide an event for registering presets at Runtime. However, you should
 * consider generating presets in the data generator whenever possible (see WoverWorldPresetProvider).
 */
public class WorldPresetManager {
    /**
     * The event that is fired when the Registry for {@link WorldPreset}s
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverWorldPresetProvider)
     * for Details.
     */
    public static final Event<OnBootstrapWorldPresets> BOOTSTRAP_WORLD_PRESETS = WorldPresetsManagerImpl.BOOTSTRAP_WORLD_PRESETS;

    /**
     * Creates a {@link ResourceKey} for a {@link WorldPreset}.
     *
     * @param loc The location of the preset.
     * @return The key.
     */
    public static ResourceKey<WorldPreset> createKey(ResourceLocation loc) {
        return WorldPresetsManagerImpl.createKey(loc);
    }

    /**
     * Gets a {@link Holder} for a {@link WorldPreset} using a {@link RegistryAccess}.
     *
     * @param access The {@link RegistryAccess}
     * @param key    The {@link ResourceKey} of the preset.
     * @return The holder or {@code null}
     */
    public static Holder<WorldPreset> get(RegistryAccess access, ResourceKey<WorldPreset> key) {
        return WorldPresetsManagerImpl.get(access, key);
    }

    /**
     * Suggests a {@link WorldPreset} as the default preset. Default presets are
     * selected when a new World is created in the UI and are set as the world Type whenever
     * a new settings file is generated on the server.
     * <p>
     * Suggested defaults have a priority. The preset with the highest priority will be
     * used as the default preset.
     */
    public static void suggestDefault(ResourceKey<WorldPreset> preset, int priority) {
        WorldPresetsManagerImpl.suggestDefault(preset, priority);
    }

    private WorldPresetManager() {
    }
}
