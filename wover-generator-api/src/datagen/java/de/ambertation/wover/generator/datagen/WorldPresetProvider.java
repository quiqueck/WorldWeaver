package de.ambertation.wover.generator.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverWorldPresetProvider;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig;
import de.ambertation.wover.generator.api.preset.WorldPresets;
import de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator;
import de.ambertation.wover.preset.api.WorldPresetManager;
import de.ambertation.wover.preset.api.WorldPresetTags;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;

public class WorldPresetProvider extends WoverWorldPresetProvider {


    /**
     * Creates a new instance of {@link WoverWorldPresetProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WorldPresetProvider(ModCore modCore) {
        super(modCore, "Wover Presets");
    }

    @Override
    protected void bootstrap(WorldPresetBootstrapContext ctx) {
        ctx.register(WorldPresets.WOVER_WORLD, createNormal(ctx));
        ctx.register(WorldPresets.WOVER_WORLD_LARGE, createLarge(ctx));
        ctx.register(WorldPresets.WOVER_WORLD_AMPLIFIED, createAmplified(ctx));
    }

    @Override
    protected void prepareTags(TagBootstrapContext<WorldPreset> provider) {
        provider.add(
                WorldPresetTags.NORMAL,
                WorldPresets.WOVER_WORLD,
                WorldPresets.WOVER_WORLD_AMPLIFIED,
                WorldPresets.WOVER_WORLD_LARGE
        );
    }

    private static WorldPreset createLegacy(WorldPresetBootstrapContext ctx) {
        return buildPreset(
                ctx.overworldStem,
                ctx.netherContext, WoverNetherConfig.MINECRAFT_17,
                ctx.endContext, WoverEndConfig.MINECRAFT_17
        );
    }

    private static WorldPreset createAmplified(WorldPresetBootstrapContext ctx) {
        Holder<NoiseGeneratorSettings> amplifiedBiomeGenerator = ctx.noiseSettings
                .getOrThrow(NoiseGeneratorSettings.AMPLIFIED);

        WorldPresetBootstrapContext.StemContext amplifiedNetherContext = WorldPresetBootstrapContext.StemContext.of(
                ctx.netherContext.dimension,
                ctx.netherContext.structureSets,
                ctx.noiseSettings.getOrThrow(WoverChunkGenerator.AMPLIFIED_NETHER)
        );

        return buildPreset(
                ctx.makeNoiseBasedOverworld(
                        ctx.overworldStem.generator().getBiomeSource(),
                        amplifiedBiomeGenerator
                ),
                amplifiedNetherContext, WoverNetherConfig.MINECRAFT_18_AMPLIFIED,
                ctx.endContext, WoverEndConfig.MINECRAFT_20_AMPLIFIED
        );
    }

    private static WorldPreset createLarge(WorldPresetBootstrapContext ctx) {
        Holder<NoiseGeneratorSettings> largeBiomeGenerator = ctx.noiseSettings
                .getOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
        return buildPreset(
                ctx.makeNoiseBasedOverworld(
                        ctx.overworldStem.generator().getBiomeSource(),
                        largeBiomeGenerator
                ),
                ctx.netherContext, WoverNetherConfig.MINECRAFT_18_LARGE,
                ctx.endContext, WoverEndConfig.MINECRAFT_20_LARGE
        );
    }

    private static WorldPreset createNormal(WorldPresetBootstrapContext ctx) {

        return buildPreset(
                ctx.overworldStem,
                ctx.netherContext, WoverNetherConfig.DEFAULT,
                ctx.endContext, WoverEndConfig.DEFAULT
        );
    }

    private static WorldPreset buildPreset(
            LevelStem overworldStem,
            WorldPresetBootstrapContext.StemContext netherContext,
            WoverNetherConfig netherConfig,
            WorldPresetBootstrapContext.StemContext endContext,
            WoverEndConfig endConfig
    ) {
        return WorldPresetManager.of(buildDimensionMap(
                overworldStem, netherContext, netherConfig, endContext, endConfig
        ));
    }

    public static Map<ResourceKey<LevelStem>, LevelStem> buildDimensionMap(
            LevelStem overworldStem,
            WorldPresetBootstrapContext.StemContext netherContext,
            WoverNetherConfig netherConfig,
            WorldPresetBootstrapContext.StemContext endContext,
            WoverEndConfig endConfig
    ) {
        return Map.of(
                LevelStem.OVERWORLD,
                overworldStem,
                LevelStem.NETHER,
                WorldPresets.makeWoverNetherStem(netherContext, netherConfig),
                LevelStem.END,
                WorldPresets.makeWoverEndStem(endContext, endConfig)
        );
    }
}
