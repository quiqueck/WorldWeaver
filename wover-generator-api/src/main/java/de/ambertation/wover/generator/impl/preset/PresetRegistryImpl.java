package de.ambertation.wover.generator.impl.preset;

import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig;
import de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator;
import de.ambertation.wover.legacy.api.LegacyHelper;
import de.ambertation.wover.preset.api.WorldPresetManager;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PresetRegistryImpl {
    public final static ResourceKey<WorldPreset> WOVER_WORLD = WorldPresetManager.createKey(LibWoverWorldGenerator.C.id(
            "normal"));
    public final static ResourceKey<WorldPreset> WOVER_WORLD_LARGE = WorldPresetManager.createKey(LibWoverWorldGenerator.C.id(
            "large"));
    public final static ResourceKey<WorldPreset> WOVER_WORLD_AMPLIFIED = WorldPresetManager.createKey(
            LibWoverWorldGenerator.C.id(
                    "amplified"));
    public final static ResourceKey<WorldPreset> BCL_WORLD_17
            = WorldPresetManager.createKey(LegacyHelper.BCLIB_CORE.id("legacy_17"));

    @NotNull
    public static LevelStem makeWoverNetherStem(
            WorldPresetBootstrapContext.StemContext context,
            WoverNetherConfig config
    ) {
        WoverNetherBiomeSource netherSource = new WoverNetherBiomeSource(config);

        return new LevelStem(
                context.dimension,
                new WoverChunkGenerator(netherSource, context.generatorSettings)
        );
    }

    public static LevelStem makeWoverEndStem(WorldPresetBootstrapContext.StemContext context, WoverEndConfig config) {
        WoverEndBiomeSource endSource = new WoverEndBiomeSource(config);
        return new LevelStem(
                context.dimension,
                new WoverChunkGenerator(endSource, context.generatorSettings)
        );
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // no-op
    }
}
