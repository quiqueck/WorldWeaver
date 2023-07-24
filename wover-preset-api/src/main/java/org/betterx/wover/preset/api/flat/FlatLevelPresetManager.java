package org.betterx.wover.preset.api.flat;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapFlatLevelPresets;
import org.betterx.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

/**
 * Utility class for {@link FlatLevelGeneratorPreset}s.
 * <p>
 * The class provide an event for registering presets at Runtime. However, you should
 * consider generating presets in the data generator whenever possible (see WoverFlatLevelPresetProvider).
 */
public class FlatLevelPresetManager {
    private FlatLevelPresetManager() {
    }

    /**
     * The event that is fired when the Registry for {@link FlatLevelGeneratorPreset}s
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverFlatLevelPresetProvider) for Details.
     */
    public static final Event<OnBootstrapFlatLevelPresets> BOOTSTRAP_FLAT_LEVEL_PRESETS = FlatLevelPresetManagerImpl.BOOTSTRAP_FLAT_LEVEL_PRESETS;

    /**
     * Creates a {@link ResourceKey} for a {@link FlatLevelGeneratorPreset}.
     *
     * @param loc The location of the preset.
     * @return The key.
     */
    public static ResourceKey<FlatLevelGeneratorPreset> createKey(ResourceLocation loc) {
        return FlatLevelPresetManagerImpl.createKey(loc);
    }
}
