package org.betterx.wover.preset.api.flat;

import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class FlatLevelPresetTags {
    public static final TagRegistry<FlatLevelGeneratorPreset, TagBootstrapContext<FlatLevelGeneratorPreset>> TAGS =
            TagManager.registerType(Registries.FLAT_LEVEL_GENERATOR_PRESET);
    public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = FlatLevelGeneratorPresetTags.VISIBLE;

    private FlatLevelPresetTags() {
    }
}
