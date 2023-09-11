package org.betterx.wover.preset.api;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapWorldPresets;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;

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
     * Gets the default {@link WorldPreset}.
     *
     * @return The default preset.
     */
    public static ResourceKey<WorldPreset> getDefault() {
        return WorldPresetsManagerImpl.getDefault();
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

    /**
     * Creates a new {@link WorldPreset} with thedimensions from the given registry.
     *
     * @param dimensions The dimensions registry.
     * @return The {@link WorldPreset}.
     */
    public static WorldPreset withDimensions(
            Registry<LevelStem> dimensions
    ) {
        return WorldPresetsManagerImpl.withDimensions(dimensions);
    }

    private WorldPresetManager() {
    }

    /**
     * Returns a Map of all dimensions for the given preset.
     *
     * @param preset The preset to read
     * @return The dimensions.
     */
    public static Map<ResourceKey<LevelStem>, LevelStem> getDimensions(Holder<WorldPreset> preset) {
        return WorldPresetsManagerImpl.getDimensions(preset);
    }

    /**
     * Gets the {@link LevelStem} for the given {@link ResourceKey}.
     *
     * @param preset The preset to read
     * @param key    The stems key
     * @return The stem.
     */
    public static LevelStem getDimension(Holder<WorldPreset> preset, ResourceKey<LevelStem> key) {
        return WorldPresetsManagerImpl.getDimension(preset, key);
    }

    /**
     * Creates a new {@link WorldPreset} from the given stems.
     *
     * @param overworldStem The overworld stem.
     * @param netherStem    The nether stem.
     * @param endStem       The end stem.
     * @return The {@link WorldPreset}.
     */
    public static WorldPreset fromStems(
            LevelStem overworldStem,
            LevelStem netherStem,
            LevelStem endStem
    ) {
        return WorldPresetsManagerImpl.fromStems(overworldStem, netherStem, endStem);
    }

    /**
     * Creates a new {@link WorldPreset} from a map of {@link LevelStem}s.
     *
     * @param dimensions The stems.
     * @return The {@link WorldPreset}.
     */
    public static WorldPreset of(Map<ResourceKey<LevelStem>, LevelStem> dimensions) {
        return new WorldPreset(dimensions);
    }
}
