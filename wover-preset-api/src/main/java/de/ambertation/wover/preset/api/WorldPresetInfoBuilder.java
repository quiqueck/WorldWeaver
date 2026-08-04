package de.ambertation.wover.preset.api;

import de.ambertation.wover.preset.impl.WorldPresetInfoBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

/**
 * Fluent builder for {@link WorldPresetInfo} entries.
 * <p>
 * Instances are created with {@link #start(BootstrapContext)} inside the bootstrap callback of the
 * {@link WorldPresetInfoRegistry#WORLD_PRESET_INFO_REGISTRY} (typically from a
 * {@code WoverRegistryContentProvider<WorldPresetInfo>} data generator), configured with the setters
 * below, and finally turned into a registry entry with {@link #register(ResourceKey)}.
 * <pre class="java"> WorldPresetInfoBuilder.start(context)
 *     .order(1000)
 *     .netherOverride(WorldPresets.NORMAL)
 *     .endOverride(WorldPresets.NORMAL)
 *     .register(MY_PRESET);</pre>
 */
public interface WorldPresetInfoBuilder {
    /**
     * Sets the sort order used to position the preset in the "Create World" preset list. Lower values
     * are sorted first.
     *
     * @param order the sort order
     * @return this builder, for chaining
     */
    WorldPresetInfoBuilder order(int order);

    /**
     * Reuses the overworld dimension of {@code overworldLike} instead of the one defined by this preset.
     *
     * @param overworldLike the preset whose overworld should be used
     * @return this builder, for chaining
     */
    WorldPresetInfoBuilder overworldOverride(ResourceKey<WorldPreset> overworldLike);

    /**
     * Reuses the nether dimension of {@code netherLike} instead of the one defined by this preset.
     *
     * @param netherLike the preset whose nether should be used
     * @return this builder, for chaining
     */
    WorldPresetInfoBuilder netherOverride(ResourceKey<WorldPreset> netherLike);

    /**
     * Reuses the end dimension of {@code endLike} instead of the one defined by this preset.
     *
     * @param endLike the preset whose end should be used
     * @return this builder, for chaining
     */
    WorldPresetInfoBuilder endOverride(ResourceKey<WorldPreset> endLike);

    /**
     * Builds the {@link WorldPresetInfo} without registering it.
     *
     * @return the new {@link WorldPresetInfo}
     */
    WorldPresetInfo build();

    /**
     * Builds the {@link WorldPresetInfo} and registers it under the same location as {@code key}
     * in the {@link WorldPresetInfoRegistry#WORLD_PRESET_INFO_REGISTRY}.
     *
     * @param key the key of the {@link WorldPreset} this info describes
     * @return a holder to the registered {@link WorldPresetInfo}
     */
    Holder<WorldPresetInfo> register(ResourceKey<WorldPreset> key);

    /**
     * Starts a new builder for the given bootstrap context.
     *
     * @param context the bootstrap context the info will be registered into
     * @return a new builder
     */
    static WorldPresetInfoBuilder start(BootstrapContext<WorldPresetInfo> context) {
        return new WorldPresetInfoBuilderImpl(context);
    }
}
