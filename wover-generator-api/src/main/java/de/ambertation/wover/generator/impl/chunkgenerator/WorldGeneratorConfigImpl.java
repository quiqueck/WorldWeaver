package de.ambertation.wover.generator.impl.chunkgenerator;

import de.ambertation.wunderlib.utils.Version;
import de.ambertation.wover.core.api.IntegrationCore;
import de.ambertation.wover.entrypoint.LibWoverEvents;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.generator.api.preset.WorldPresets;
import de.ambertation.wover.generator.impl.preset.PresetRegistryImpl;
import de.ambertation.wover.legacy.api.LegacyHelper;
import de.ambertation.wover.preset.api.WorldPresetInfo;
import de.ambertation.wover.preset.api.WorldPresetInfoRegistry;
import de.ambertation.wover.preset.api.WorldPresetManager;
import de.ambertation.wover.state.api.WorldConfig;
import de.ambertation.wover.state.api.WorldState;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldGeneratorConfigImpl {
    public static final String TAG_PRESET = "preset";
    private static final String LEGACY_TAG_GENERATOR = "generator";
    public static final String TAG_DIMENSIONS = "dimensions";
    public static final String TAG_DIMENSION_PRESETS = "world_presets";
    private static final String LEGACY_TAG_VERSION = "version";
    private static final String LEGACY_TAG_BN_GEN_VERSION = "generator_version";
    private static DimensionsWrapper DEFAULT_DIMENSIONS_WRAPPER = null;

    static @NotNull CompoundTag getPresetsNbt() {
        return WorldConfig.getCompoundTag(LibWoverWorldGenerator.C, TAG_PRESET);
    }

    static @NotNull CompoundTag getPresetsNbtFromFolder(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        final File dataDir = levelStorageAccess.getLevelPath(LevelResource.ROOT).resolve("data").toFile();
        File nbtFile = new File(dataDir, LibWoverWorldGenerator.C.modId + ".nbt");
        CompoundTag root = null;
        if (nbtFile.exists()) {
            try {
                root = NbtIo.readCompressed(nbtFile.toPath(), NbtAccounter.create(0x200000L));
            } catch (IOException e) {
                LibWoverEvents.C.log.error("NBT loading failed", e);
            }
        }
        if (root != null)
            return root.getCompound(TAG_PRESET).orElse(new CompoundTag());

        return new CompoundTag();
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
        writeWorldPresetSettingsDirect(null, settings);
    }

    /**
     * @param access The registry provider the {@link ChunkGenerator}s in {@code settings} were resolved
     *               against, or {@code null} to fall back to {@link WorldState#allStageRegistryAccess()}.
     *               See {@link #writeWorldPresetSettings} for why passing the right one matters.
     */
    public static void writeWorldPresetSettingsDirect(
            @Nullable HolderLookup.Provider access,
            Map<ResourceKey<LevelStem>, ChunkGenerator> settings
    ) {
        DimensionsWrapper wrapper = new DimensionsWrapper(settings);
        writeWorldPresetSettings(access, wrapper);
    }

    /**
     * Encodes the per-dimension generator settings into this mod's world NBT.
     * <p>
     * The {@code access} matters, and passing the wrong one fails in a way that is easy to miss. A
     * {@link ChunkGenerator} encodes its {@code settings} through
     * {@link net.minecraft.world.level.levelgen.NoiseGeneratorSettings#CODEC}, a registry-element codec, so
     * every {@code Holder.Reference} it walks is checked for membership in the registry carried by the
     * {@link RegistryOps}. That check is by identity of the owning registry, not by key: handing it a
     * <em>different</em> {@link RegistryAccess} that happens to contain the same ids still fails with
     * "Element Reference{...} is not valid in current registry", and the whole encode returns an error.
     * <p>
     * That is exactly what used to happen for every newly created world. This ran during
     * {@code CREATED_NEW_WORLD_FOLDER} against the global {@link WorldState#allStageRegistryAccess()},
     * which at that point is not the registry access the world's own {@link WorldDimensions} were built
     * from, so nothing was ever written for a new world. The failure then went unnoticed because
     * {@link #migrateGeneratorSettings()} runs later, finds the empty tag, reports "Found World without
     * generator Settings" for a world created moments earlier, and writes the hard-coded
     * {@link WorldPresets#WOVER_WORLD} default instead - silently discarding whichever preset the world was
     * actually created with. It looked correct only for worlds created with that default.
     * <p>
     * {@code WorldLifecycle.CREATED_NEW_WORLD_FOLDER} already hands out the matching provider, so the
     * fix is simply to use it rather than reaching for the global. The migration path has no such provider
     * and keeps the fallback; by the time it runs, the global access is the world's own.
     */
    private static void writeWorldPresetSettings(
            @Nullable HolderLookup.Provider access,
            DimensionsWrapper wrapper
    ) {
        final RegistryOps<Tag> registryOps = RegistryOps.create(
                NbtOps.INSTANCE,
                access != null ? access : WorldState.allStageRegistryAccess()
        );
        final var encodeResult = DimensionsWrapper.CODEC.encodeStart(registryOps, wrapper);

        if (encodeResult.result().isPresent()) {
            final CompoundTag settingsNbt = WorldConfig.getRootTag(LibWoverWorldGenerator.C);
            settingsNbt.put(TAG_PRESET, encodeResult.result().get());
        } else {
            // Include the codec's own message: without it this failure is indistinguishable from a dozen
            // different causes, which is how the registry-mismatch above stayed hidden.
            LibWoverWorldGenerator.C.log.error(
                    "Unable to encode world generator settings for level.dat: "
                            + encodeResult.error().map(e -> e.message()).orElse("unknown error")
            );
        }

        WorldConfig.saveFile(LibWoverWorldGenerator.C);
    }

    // These are ResourceLocation namespaces, so they come from ModCore#namespace and not from
    // ModCore#modId - for this module the two differ ("wover-generator" vs. "wover").
    /** Namespace the generator types used before the move from BCLib/"Worlds Together" to WorldWeaver. */
    private static final String LEGACY_NAMESPACE = LegacyHelper.BCLIB_CORE.namespace + ":";
    /** Namespace the generator types live in today. */
    private static final String WOVER_NAMESPACE = LibWoverWorldGenerator.C.namespace + ":";

    /**
     * Rewrites the chunk-generator, biome-source and noise-settings ids of a legacy dimension map
     * from the {@code bclib} namespace to {@code wover}.
     * <p>
     * "Worlds Together" stored these type ids under the old namespace ({@code bclib:betterx},
     * {@code bclib:end_biome_source}, {@code bclib:nether_biome_source},
     * {@code bclib:amplified_nether}, ...). None of them are registered any more, so copying the
     * dimension map over verbatim leaves the world with generator types that no codec can resolve
     * and the world fails to load.
     * <p>
     * Only the three keys that carry a registry id are touched, so entries that are still
     * legitimately {@code bclib}-namespaced &mdash; such as {@link PresetRegistryImpl#BCL_WORLD_17}
     * &mdash; are left alone.
     *
     * @param dimensions The legacy {@code dimensions} compound. Modified in place.
     */
    private static void migrateLegacyDimensionIDs(CompoundTag dimensions) {
        for (String dimensionKey : dimensions.keySet()) {
            final CompoundTag stem = dimensions.getCompound(dimensionKey).orElse(null);
            if (stem == null) continue;

            migrateLegacyID(stem, "type");
            migrateLegacyID(stem, "settings");
            stem.getCompound("biome_source").ifPresent(source -> migrateLegacyID(source, "type"));
        }
    }

    /**
     * Moves a single id from the legacy namespace into the current one, if it is in the legacy
     * namespace at all.
     *
     * @param tag The compound holding the id
     * @param key The key of the id within {@code tag}
     */
    private static void migrateLegacyID(CompoundTag tag, String key) {
        final String id = tag.getString(key).orElse(null);
        if (id == null || !id.startsWith(LEGACY_NAMESPACE)) return;

        final String migrated = WOVER_NAMESPACE + id.substring(LEGACY_NAMESPACE.length());
        LibWoverWorldGenerator.C.log.info("Migrating legacy generator ID '" + id + "' to '" + migrated + "'.");
        tag.putString(key, migrated);
    }

    public static void migrateGeneratorSettings() {
        final CompoundTag settingsNbt = getPresetsNbt();

        if (settingsNbt.isEmpty()) {
            CompoundTag wtGen = getLegacyPresetsNbt();
            if (wtGen != null && wtGen.contains(TAG_DIMENSIONS)) {
                LibWoverWorldGenerator.C.log.info("Found World with WorldsTogether Settings.");
                CompoundTag newPresets = getPresetsNbt();
                CompoundTag dimensions = wtGen.getCompound(TAG_DIMENSIONS).orElse(null);
                if (dimensions != null) migrateLegacyDimensionIDs(dimensions);
                newPresets.put(TAG_DIMENSIONS, wtGen.get(TAG_DIMENSIONS));

                WorldConfig.saveFile(LibWoverWorldGenerator.C);
                return;
            }

            CompoundTag oldGen = getLegacyGeneratorNbt();
            if (oldGen != null) {
                if (oldGen.contains("type")) {
                    LibWoverWorldGenerator.C.log.info("Found World with beta generator Settings.");
                    if ("bclib:bcl_world_preset_settings".equals(oldGen.getString("type"))) {
                        int netherVersion = oldGen.getInt("minecraft:the_nether").orElse(18);
                        int endVersion = oldGen.getInt("minecraft:the_end").orElse(18);

                        if (netherVersion == 18) netherVersion = 0;
                        else if (netherVersion == 17) netherVersion = 1;
                        else netherVersion = 2;

                        if (endVersion == 18) endVersion = 0;
                        else if (endVersion == 17) endVersion = 1;
                        else endVersion = 2;

                        var presets = List.of(
                                DimensionsWrapper.getDimensionsMap(WorldPresets.WOVER_WORLD),
                                DimensionsWrapper.getDimensionsMap(PresetRegistryImpl.BCL_WORLD_17),
                                DimensionsWrapper.getDimensionsMap(net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL)
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

            LibWoverWorldGenerator.C.log.info("Found World without generator Settings. Setting up data...");
            ResourceKey<WorldPreset> biomeSourceVersion = WorldPresets.WOVER_WORLD;

            // WorldConfig will set the version tag to 9.9.9 if the bclib file does not exist.
            // This is to prevent the world from being loaded as a legacy bclib world.
            final CompoundTag bclRoot = WorldConfig.getRootTag(LegacyHelper.BCLIB_CORE);

            Version bclVersion = new Version(bclRoot.getString(LEGACY_TAG_VERSION).orElse("0.0.0"));
            boolean isPre18 = !bclVersion.isLargerOrEqualVersion("1.0.0");

            if (isPre18) {
                LibWoverWorldGenerator.C.log.info("World was create pre 1.18!");
                biomeSourceVersion = PresetRegistryImpl.BCL_WORLD_17;
            }

            if (WorldConfig.hasMod(IntegrationCore.BETTER_NETHER)) {
                LibWoverWorldGenerator.C.log.info("Found Data from BetterNether, using for migration.");
                final CompoundTag bnRoot = WorldConfig.getRootTag(IntegrationCore.BETTER_NETHER);
                biomeSourceVersion = "1.17".equals(bnRoot.getString(LEGACY_TAG_BN_GEN_VERSION))
                        ? PresetRegistryImpl.BCL_WORLD_17
                        : WorldPresets.WOVER_WORLD;
            }

            WorldDimensions dimensions = DimensionsWrapper.getDimensions(biomeSourceVersion);
            if (dimensions != null) {
                LibWoverWorldGenerator.C.log.info("Set world to BiomeSource Version " + biomeSourceVersion);
                // Migration path: no event-supplied provider here, so fall back to the global access.
                writeWorldPresetSettings(null, new DimensionsWrapper(dimensions));
            } else {
                LibWoverWorldGenerator.C.log.error("Failed to set world to BiomeSource Version " + biomeSourceVersion);
            }
        }
    }

    public static void createWorldConfig(
            @Nullable HolderLookup.Provider access,
            Holder<WorldPreset> currentPreset,
            WorldDimensions dimensions
    ) {
        //make sure we store the preset key in the all generators that currently do not have one
        if (currentPreset != null && currentPreset.unwrapKey().isPresent()) {
            final WorldPresetInfo info = WorldPresetInfoRegistry.getFor(currentPreset);
            final ResourceKey<WorldPreset> presetKey = currentPreset.unwrapKey().orElseThrow();
            for (var dimEntry : dimensions.dimensions().entrySet()) {
                if (dimEntry.getValue().generator() instanceof ConfiguredChunkGenerator cfg) {
                    final ResourceKey<WorldPreset> secondaryPreset = info.getPresetOverrideRecursive(dimEntry.getKey());
                    if (cfg.wover_getConfiguredWorldPreset() == null) {
                        cfg.wover_setConfiguredWorldPreset(secondaryPreset != null ? secondaryPreset : presetKey);
                    }
                }
            }
        }

        LibWoverWorldGenerator.C.log.verbose("Creating presets file for new world");
        writeWorldPresetSettingsDirect(access, DimensionsWrapper.build(dimensions));
    }

    public static @NotNull Map<ResourceKey<LevelStem>, ChunkGenerator> loadWorldDimensions(
            RegistryAccess registryAccess,
            CompoundTag presetNBT
    ) {
        try {
            final RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
            if (DEFAULT_DIMENSIONS_WRAPPER == null) {
                DEFAULT_DIMENSIONS_WRAPPER = new DimensionsWrapper(DimensionsWrapper.getDimensionsMap(
                        registryAccess,
                        WorldPresetManager.getDefault()
                ));
            }

            if (presetNBT == null || !presetNBT.contains(TAG_DIMENSIONS)) {
                return DEFAULT_DIMENSIONS_WRAPPER.dimensions;
            }

            Optional<DimensionsWrapper> oLevelStem = DimensionsWrapper.CODEC
                    .parse(new Dynamic<>(registryOps, presetNBT))
                    .resultOrPartial(LibWoverWorldGenerator.C.log::error);

            return oLevelStem.orElse(DEFAULT_DIMENSIONS_WRAPPER).dimensions;
        } catch (Exception e) {
            LibWoverWorldGenerator.C.log.error("Failed to load Dimensions", e);
            return DEFAULT_DIMENSIONS_WRAPPER.dimensions;
        }
    }
}
