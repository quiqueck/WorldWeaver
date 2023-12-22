package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.mixin.generator.ChunkGeneratorAccessor;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldConfig;
import org.betterx.wover.state.api.WorldState;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkGeneratorManagerImpl {
    private static final ResourceLocation LEGACY_ID = LegacyHelper.BCLIB_CORE.convertNamespace(WoverChunkGenerator.ID);

    private static final List<String> GENERATOR_IDS = new ArrayList<>(1);

    /**
     * We need this for mods that use the DSL system to actually fix the generator data.
     * The DSL will check if the generator type is actually known, so we need to inject
     * our own generator into the DSL system.
     *
     * @param map A DSL map, that could contain anything. The Method checks if it contains
     *            the flat generator ("minecraft:flat") and if so, it will inject the
     *            Wover generator into the map
     * @return The altered map
     */
    @ApiStatus.Internal
    public static Map<String, Supplier<TypeTemplate>> addGeneratorDSL(Map<String, Supplier<TypeTemplate>> map) {
        if (map.containsKey("minecraft:flat") && !ModCore.isDatagen()) {
            Map<String, Supplier<TypeTemplate>> nMap = new HashMap<>(map);
            GENERATOR_IDS.forEach(id -> nMap.put(id, DSL::remainder));
            return ImmutableMap.copyOf(nMap);
        }
        return map;
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(WoverChunkGenerator.ID, WoverChunkGenerator.CODEC);
        if (LegacyHelper.isLegacyEnabled()) {
            register(LEGACY_ID, LegacyHelper.wrap(WoverChunkGenerator.CODEC));
        }
        WorldConfig.registerMod(WoverWorldGenerator.C);

        WorldLifecycle.CREATED_NEW_WORLD_FOLDER.subscribe(ChunkGeneratorManagerImpl::onWorldCreation, 2000);
    }

    private static void onWorldCreation(
            LevelStorageSource.LevelStorageAccess storage,
            RegistryAccess access,
            Holder<WorldPreset> currentPreset,
            WorldDimensions dimensions,
            boolean recreated
    ) {
        WorldGeneratorConfigImpl.createWorldConfig(currentPreset, dimensions);
    }

    public static void onWorldReCreate(
            LevelStorageSource.LevelStorageAccess storage,
            WorldCreationContext context
    ) {
        final var configuredPreset = WorldGeneratorConfigImpl.getPresetsNbtFromFolder(storage);
        final var dimensions = WorldGeneratorConfigImpl.loadWorldDimensions(
                WorldState.allStageRegistryAccess(),
                configuredPreset
        );

        for (var dimEntry : context.selectedDimensions().dimensions().entrySet()) {
            final var refDim = dimensions.get(dimEntry.getKey());
            if (refDim instanceof ConfiguredChunkGenerator refGen
                    && refGen.wover_getConfiguredWorldPreset() != null
                    && dimEntry.getValue().generator() instanceof ConfiguredChunkGenerator loadGen
                    && loadGen.wover_getConfiguredWorldPreset() == null) {
                loadGen.wover_setConfiguredWorldPreset(refGen.wover_getConfiguredWorldPreset());
            }
        }
    }

    public static void register(ResourceLocation location, Codec<? extends ChunkGenerator> codec) {
        final String idString = location.toString();
        if (GENERATOR_IDS.contains(idString)) {
            throw new IllegalStateException("Duplicate generator id: " + idString);
        }
        GENERATOR_IDS.add(idString);
        BuiltInRegistryManager.register(BuiltInRegistries.CHUNK_GENERATOR, location, codec);
    }

    public static String enumerateFeatureNamespaces(@NotNull ChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof ChunkGeneratorAccessor acc) {
            var supplier = acc.wover_getFeaturesPerStep();
            if (supplier != null) {
                List<FeatureSorter.StepFeatureData> list = supplier.get();
                final HashMap<String, Integer> namespaces = new HashMap<>();
                if (list != null) {
                    for (var features : list)
                        if (features != null) {
                            for (PlacedFeature feature : features.features()) {
                                if (feature != null && feature.feature() != null) {
                                    final String namespace;
                                    if (feature.feature()
                                               .unwrapKey()
                                               .isPresent()) {
                                        namespace = feature
                                                .feature()
                                                .unwrapKey()
                                                .get()
                                                .location()
                                                .getNamespace();
                                    } else {
                                        namespace = "direct_holder";
                                    }

                                    namespaces.put(namespace, namespaces.getOrDefault(namespace, 0) + 1);
                                }
                            }
                        }
                }
                return namespaces.entrySet()
                                 .stream()
                                 .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                                 .reduce((a, b) -> a + ", " + b)
                                 .orElse("none");
            }
        }
        return "unknown";
    }

    public static String printGeneratorInfo(@Nullable String className, @NotNull ChunkGenerator generator) {
        StringBuilder sb = new StringBuilder();
        sb.append(className == null ? generator.getClass().getSimpleName() : className)
          .append(" (")
          .append(Integer.toHexString(generator.hashCode()))
          .append(")");

        if (generator instanceof ConfiguredChunkGenerator cfg) {
            final var preset = cfg.wover_getConfiguredWorldPreset();
            sb.append("\n    preset     = ").append(preset == null ? "none" : preset.location());
        }

        if (generator instanceof NoiseBasedChunkGenerator noise) {
            final var key = noise.generatorSettings().unwrapKey();
            sb.append("\n    noise      = ").append(key.isEmpty() ? "custom" : key.get().location());
        }

        if (generator instanceof ChunkGeneratorAccessor) {
            sb.append("\n    features   = ").append(enumerateFeatureNamespaces(generator));
        }

        return sb.toString();
    }

    public static void printDimensionInfo(Registry<LevelStem> dimensionRegistry) {
        if (!Configs.MAIN.verboseLogging.get()) return;

        printDimensionInfo("World Dimensions", dimensionRegistry);
    }

    public static void printDimensionInfo(String title, Registry<LevelStem> dimensionRegistry) {
        StringBuilder output = new StringBuilder(title + ": ");
        for (var entry : dimensionRegistry.entrySet()) {
            output.append("\n - ").append(entry.getKey().location()).append(": ")
                  .append("\n     ").append(
                          entry.getValue()
                               .generator()
                               .toString()
                               .replace("\n", "\n     ")
                  )
                  .append("\n     ")
                  .append(
                          entry.getValue()
                               .generator()
                               .getBiomeSource()
                               .toString()
                               .replace("\n", "\n     ")
                  );
        }

        WoverWorldGenerator.C.log.info(output.toString());
    }
}
