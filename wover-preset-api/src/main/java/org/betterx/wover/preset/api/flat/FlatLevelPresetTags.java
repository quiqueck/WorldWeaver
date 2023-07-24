package org.betterx.wover.preset.api.flat;

import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

/**
 * Provides access to the Tags for {@link FlatLevelGeneratorPreset}s
 * <p>
 * If you want Presets to show up in the UI you need to add them to the
 * {@link #VISIBLE} tag, for a given Tag named {@code FLAT_NETHER}
 * you would run:
 * <pre class="java"> FlatLevelPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
 *     ctx.add(FlatLevelPresetTags.VISIBLE, FLAT_NETHER);
 * });</pre>
 */
public class FlatLevelPresetTags {
    /**
     * The {@link TagRegistry} for {@link FlatLevelGeneratorPreset}s
     */
    public static final TagRegistry<FlatLevelGeneratorPreset, TagBootstrapContext<FlatLevelGeneratorPreset>> TAGS =
            TagManager.registerType(Registries.FLAT_LEVEL_GENERATOR_PRESET);

    /**
     * Tag that determines weather a {@link FlatLevelGeneratorPreset} is visible in the UI.
     */
    public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = FlatLevelGeneratorPresetTags.VISIBLE;

    private FlatLevelPresetTags() {
    }
}
