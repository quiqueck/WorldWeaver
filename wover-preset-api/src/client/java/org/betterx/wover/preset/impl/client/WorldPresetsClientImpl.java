package org.betterx.wover.preset.impl.client;

import org.betterx.wover.preset.api.client.WorldPresetsUI;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WorldPresetsClientImpl {

    private static final List<WorldPresetsUI.PresetEditorGetter> EDITORS = new ArrayList<>(4);

    public static void registerCustomUI(WorldPresetsUI.PresetEditorGetter getter) {
        if (getter != null) {
            EDITORS.add(getter);
        }
    }

    public static boolean isKey(Holder<WorldPreset> holder, ResourceKey<WorldPreset> keyToTest) {
        return (holder != null && keyToTest.equals(holder.unwrapKey().orElse(null)));
    }

    public static void registerCustomUI(ResourceKey<WorldPreset> key, PresetEditor setupScreen) {
        if (setupScreen != null) {
            EDITORS.add(holder -> {
                if (isKey(holder, key)) {
                    return setupScreen;
                }

                return null;
            });
        }
    }


    public static PresetEditor getSetupScreenForPreset(Holder<WorldPreset> holder) {
        if (holder != null) {
            for (WorldPresetsUI.PresetEditorGetter getter : EDITORS) {
                PresetEditor editor = getter.get(holder);
                if (editor != null) {
                    return editor;
                }
            }
        }
        return null;
    }

    public static void setupClientside() {
    }
}
