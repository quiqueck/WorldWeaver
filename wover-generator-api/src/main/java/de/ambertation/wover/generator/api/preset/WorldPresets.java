package de.ambertation.wover.generator.api.preset;

import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig;
import de.ambertation.wover.generator.impl.preset.PresetRegistryImpl;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

/**
 * WoVer's built-in {@link WorldPreset}s, and helpers to build a {@link LevelStem} for a {@code wover:betterx}
 * ({@link de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator WoverChunkGenerator}) Nether or
 * End dimension for use in a custom {@link WorldPreset}.
 * <p>
 * Use {@link de.ambertation.wover.preset.api.WorldPresetManager#suggestDefault(ResourceKey, int)} to make
 * {@link #WOVER_WORLD} (or a custom preset built from {@link #makeWoverNetherStem}/{@link #makeWoverEndStem})
 * the suggested default in the world creation screen.
 */
public class WorldPresets {
    /**
     * WoVer's default world preset: vanilla Overworld with a
     * {@link de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig#DEFAULT default}
     * {@link de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig WoverNetherConfig} Nether
     * and a {@link de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig#DEFAULT default}
     * {@link de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig WoverEndConfig} End.
     */
    public final static ResourceKey<WorldPreset> WOVER_WORLD = PresetRegistryImpl.WOVER_WORLD;
    /**
     * A larger-biomes variant of {@link #WOVER_WORLD}.
     */
    public final static ResourceKey<WorldPreset> WOVER_WORLD_LARGE = PresetRegistryImpl.WOVER_WORLD_LARGE;
    /**
     * The amplified-terrain variant of {@link #WOVER_WORLD}.
     */
    public final static ResourceKey<WorldPreset> WOVER_WORLD_AMPLIFIED = PresetRegistryImpl.WOVER_WORLD_AMPLIFIED;


    /**
     * Builds a {@link LevelStem} for a Nether dimension using WoVer's
     * {@link de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator WoverChunkGenerator} and a
     * {@link de.ambertation.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource WoverNetherBiomeSource}
     * configured with {@code config}.
     *
     * @param context the stem context (dimension type, structure sets, noise settings) to build the stem
     *                with, typically {@link WorldPresetBootstrapContext#netherContext}
     * @param config  the Nether biome placement configuration
     * @return the new {@link LevelStem}
     */
    public static LevelStem makeWoverNetherStem(
            WorldPresetBootstrapContext.StemContext context,
            WoverNetherConfig config
    ) {
        return PresetRegistryImpl.makeWoverNetherStem(context, config);
    }

    /**
     * Builds a {@link LevelStem} for an End dimension using WoVer's
     * {@link de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator WoverChunkGenerator} and a
     * {@link de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource WoverEndBiomeSource}
     * configured with {@code config}.
     *
     * @param context the stem context (dimension type, structure sets, noise settings) to build the stem
     *                with, typically {@link WorldPresetBootstrapContext#endContext}
     * @param config  the End biome placement configuration
     * @return the new {@link LevelStem}
     */
    public static LevelStem makeWoverEndStem(WorldPresetBootstrapContext.StemContext context, WoverEndConfig config) {
        return PresetRegistryImpl.makeWoverEndStem(context, config);
    }
}
