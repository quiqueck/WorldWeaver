package org.betterx.wover.preset.api;

import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresetTags {
    public static final TagRegistry<WorldPreset, TagBootstrapContext<WorldPreset>> TAGS =
            TagManager.registerType(Registries.WORLD_PRESET);
    public static final TagKey<WorldPreset> NORMAL = net.minecraft.tags.WorldPresetTags.NORMAL;
    public static final TagKey<WorldPreset> EXTENDED = net.minecraft.tags.WorldPresetTags.EXTENDED;

    private WorldPresetTags() {
    }
}
