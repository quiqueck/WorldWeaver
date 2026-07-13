package org.betterx.wover.preset.api.client;

import org.betterx.wover.preset.impl.client.WorldPresetsClientImpl;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


/**
 * API to register custom Setup UIs for {@link net.minecraft.world.level.levelgen.presets.WorldPreset}s
 */
@Environment(EnvType.CLIENT)
public class WorldPresetsUI {
    /**
     * Resolves the {@link PresetEditor} to show for a given {@link WorldPreset}.
     * <p>
     * Used by {@link #registerCustomUI(PresetEditorGetter)} for cases where a single implementation
     * has to decide the editor for more than one preset (e.g. based on {@link #isKey(Holder, ResourceKey)}).
     */
    public interface PresetEditorGetter {
        /**
         * Gets the {@link PresetEditor} to use for the given preset.
         *
         * @param holder the holder of the preset that is being configured
         * @return the editor to use, or {@code null} if this getter does not provide a custom editor for
         *         {@code holder}
         */
        PresetEditor get(Holder<WorldPreset> holder);
    }

    /**
     * Registers a custom Setup UI for a single {@link WorldPreset}.
     * <p>
     * The setup UI is the screen shown when a player clicks "Customize" for the preset on the
     * "Create World" screen.
     *
     * @param key         The key of the preset.
     * @param setupScreen The setup screen.
     */
    public static void registerCustomUI(ResourceKey<WorldPreset> key, PresetEditor setupScreen) {
        WorldPresetsClientImpl.registerCustomUI(key, setupScreen);
    }

    /**
     * Registers a custom Setup UI provider that can decide, for any {@link WorldPreset}, which
     * {@link PresetEditor} (if any) should be shown for it.
     *
     * @param getter the provider to register
     */
    public static void registerCustomUI(WorldPresetsUI.PresetEditorGetter getter) {
        WorldPresetsClientImpl.registerCustomUI(getter);
    }

    /**
     * Checks whether the given {@link Holder} points to the given {@link ResourceKey}.
     *
     * @param holder    the holder to test, may be unbound
     * @param keyToTest the key to compare against
     * @return {@code true} if {@code holder} resolves to {@code keyToTest}
     */
    public static boolean isKey(Holder<WorldPreset> holder, ResourceKey<WorldPreset> keyToTest) {
        return WorldPresetsClientImpl.isKey(holder, keyToTest);
    }
}
