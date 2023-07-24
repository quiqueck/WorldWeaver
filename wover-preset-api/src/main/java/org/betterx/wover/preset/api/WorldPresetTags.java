package org.betterx.wover.preset.api;

import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

/**
 * Provides access to the Tags for {@link WorldPreset}s
 * <p>
 * If you want Presets to show up in the UI you need to add them to the
 * {@link #NORMAL} tag, for a given Tag named {@code END_START}
 * you would run:
 * <pre class="java"> WorldPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
 *     ctx.add(WorldPresetTags.NORMAL, END_START);
 * });</pre>
 */
public class WorldPresetTags {
    /**
     * The {@link TagRegistry} for {@link WorldPreset}s
     */
    public static final TagRegistry<WorldPreset, TagBootstrapContext<WorldPreset>> TAGS =
            TagManager.registerType(Registries.WORLD_PRESET);
    /**
     * Add your {@link WorldPreset}s to this tag to make them show up in the UI
     */
    public static final TagKey<WorldPreset> NORMAL = net.minecraft.tags.WorldPresetTags.NORMAL;
    /**
     * All vanilla {@link WorldPreset}s are in this tag as well.
     */
    public static final TagKey<WorldPreset> EXTENDED = net.minecraft.tags.WorldPresetTags.EXTENDED;

    private WorldPresetTags() {
    }
}
