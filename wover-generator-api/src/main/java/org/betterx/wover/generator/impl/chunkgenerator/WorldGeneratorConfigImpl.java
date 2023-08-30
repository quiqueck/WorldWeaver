package org.betterx.wover.generator.impl.chunkgenerator;

import de.ambertation.wunderlib.utils.Version;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.generator.api.preset.PresetsRegistry;
import org.betterx.wover.generator.impl.preset.PresetRegistryImpl;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldConfig;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class WorldGeneratorConfigImpl {
    public static final String TAG_PRESET = "preset";
    private static final String LEGACY_TAG_GENERATOR = "generator";
    public static final String TAG_DIMENSIONS = "dimensions";
    private static final String LEGACY_TAG_VERSION = "version";
    private static final String LEGACY_TAG_BN_GEN_VERSION = "generator_version";

    static @NotNull CompoundTag getPresetsNbt() {
        return WorldConfig.getCompoundTag(WoverWorldGenerator.C, TAG_PRESET);
    }

    private static @NotNull CompoundTag getLegacyPresetsNbt() {
        return WorldConfig.getCompoundTag(LegacyHelper.WORLDS_TOGETHER_CORE, TAG_PRESET);
    }

    private static CompoundTag getLegacyGeneratorNbt() {
        CompoundTag root = WorldConfig.getRootTag(LegacyHelper.WORLDS_TOGETHER_CORE);
        if (root.contains(LEGACY_TAG_GENERATOR))
            return WorldConfig.getCompoundTag(LegacyHelper.WORLDS_TOGETHER_CORE, LEGACY_TAG_GENERATOR);
        return null;
    }

    public static void writeWorldPresetSettingsDirect(Map<ResourceKey<LevelStem>, ChunkGenerator> settings) {
        DimensionsWrapper wrapper = new DimensionsWrapper(settings);
        writeWorldPresetSettings(wrapper);
    }

    private static void writeWorldPresetSettings(DimensionsWrapper wrapper) {
        final RegistryOps<Tag> registryOps = RegistryOps.create(
                NbtOps.INSTANCE,
                WorldState.allStageRegistryAccess()
        );
        final var encodeResult = DimensionsWrapper.CODEC.encodeStart(registryOps, wrapper);

        if (encodeResult.result().isPresent()) {
            final CompoundTag settingsNbt = WorldConfig.getRootTag(WoverWorldGenerator.C);
            settingsNbt.put(TAG_PRESET, encodeResult.result().get());
        } else {
            WoverWorldGenerator.C.log.error("Unable to encode world generator settings for level.dat.");
        }

        WorldConfig.saveFile(WoverWorldGenerator.C);
    }

    public static void migrateGeneratorSettings() {
        final CompoundTag settingsNbt = getPresetsNbt();

        if (settingsNbt.isEmpty()) {
            CompoundTag wtGen = getLegacyPresetsNbt();
            if (wtGen != null && wtGen.contains(TAG_DIMENSIONS)) {
                WoverWorldGenerator.C.log.info("Found World with WorldsTogether Settings.");
                CompoundTag newPresets = getPresetsNbt();
                newPresets.put(TAG_DIMENSIONS, wtGen.get(TAG_DIMENSIONS));

                WorldConfig.saveFile(WoverWorldGenerator.C);
                return;
            }

            CompoundTag oldGen = getLegacyGeneratorNbt();
            if (oldGen != null) {
                if (oldGen.contains("type")) {
                    WoverWorldGenerator.C.log.info("Found World with beta generator Settings.");
                    if ("bclib:bcl_world_preset_settings".equals(oldGen.getString("type"))) {
                        int netherVersion = 18;
                        int endVersion = 18;
                        if (oldGen.contains("minecraft:the_nether"))
                            netherVersion = oldGen.getInt("minecraft:the_nether");
                        if (oldGen.contains("minecraft:the_end"))
                            endVersion = oldGen.getInt("minecraft:the_end");

                        if (netherVersion == 18) netherVersion = 0;
                        else if (netherVersion == 17) netherVersion = 1;
                        else netherVersion = 2;

                        if (endVersion == 18) endVersion = 0;
                        else if (endVersion == 17) endVersion = 1;
                        else endVersion = 2;

                        var presets = List.of(
                                DimensionsWrapper.getDimensionsMap(PresetsRegistry.WOVER_WORLD),
                                DimensionsWrapper.getDimensionsMap(PresetRegistryImpl.BCL_WORLD_17),
                                DimensionsWrapper.getDimensionsMap(WorldPresets.NORMAL)
                        );
                        Map<ResourceKey<LevelStem>, ChunkGenerator> dimensions = new HashMap<>();
                        dimensions.put(LevelStem.OVERWORLD, presets.get(0).get(LevelStem.OVERWORLD));
                        dimensions.put(LevelStem.NETHER, presets.get(netherVersion).get(LevelStem.NETHER));
                        dimensions.put(LevelStem.END, presets.get(endVersion).get(LevelStem.END));

                        writeWorldPresetSettingsDirect(dimensions);
                    }
                    return;
                }
            }

            WoverWorldGenerator.C.log.info("Found World without generator Settings. Setting up data...");
            ResourceKey<WorldPreset> biomeSourceVersion = PresetsRegistry.WOVER_WORLD;

            // WorldConfig will set the version tag to 9.9.9 if the bclib file does not exist.
            // This is to prevent the world from being loaded as a legacy bclib world.
            final CompoundTag bclRoot = WorldConfig.getRootTag(LegacyHelper.BCLIB_CORE);

            Version bclVersion = new Version("0.0.0");
            if (bclRoot.contains(LEGACY_TAG_VERSION)) {
                bclVersion = new Version(bclRoot.getString(LEGACY_TAG_VERSION));
            }
            boolean isPre18 = !bclVersion.isLargerOrEqualVersion("1.0.0");

            if (isPre18) {
                WoverWorldGenerator.C.log.info("World was create pre 1.18!");
                biomeSourceVersion = PresetRegistryImpl.BCL_WORLD_17;
            }

            if (WorldConfig.hasMod(IntegrationCore.BETTER_NETHER)) {
                WoverWorldGenerator.C.log.info("Found Data from BetterNether, using for migration.");
                final CompoundTag bnRoot = WorldConfig.getRootTag(IntegrationCore.BETTER_NETHER);
                biomeSourceVersion = "1.17".equals(bnRoot.getString(LEGACY_TAG_BN_GEN_VERSION))
                        ? PresetRegistryImpl.BCL_WORLD_17
                        : PresetsRegistry.WOVER_WORLD;
            }

            Registry<LevelStem> dimensions = DimensionsWrapper.getDimensions(biomeSourceVersion);
            if (dimensions != null) {
                WoverWorldGenerator.C.log.info("Set world to BiomeSource Version " + biomeSourceVersion);
                writeWorldPresetSettings(new DimensionsWrapper(dimensions));
            } else {
                WoverWorldGenerator.C.log.error("Failed to set world to BiomeSource Version " + biomeSourceVersion);
            }
        }
    }

    public static void createWorldConfig(WorldDimensions dimensions) {
        WoverWorldGenerator.C.log.verbose("Creating presets file for new world");
        writeWorldPresetSettingsDirect(DimensionsWrapper.build(dimensions.dimensions()));
    }
}
