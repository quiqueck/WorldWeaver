package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.config.api.Configs;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.api.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldConfig;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;

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
        register(LEGACY_ID, LegacyHelper.wrap(WoverChunkGenerator.CODEC));
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
        WorldGeneratorConfigImpl.createWorldConfig(dimensions);
    }

    public static void register(ResourceLocation location, Codec<? extends ChunkGenerator> codec) {
        final String idString = location.toString();
        if (GENERATOR_IDS.contains(idString)) {
            throw new IllegalStateException("Duplicate generator id: " + idString);
        }
        GENERATOR_IDS.add(idString);
        BuiltInRegistryManager.register(BuiltInRegistries.CHUNK_GENERATOR, location, codec);
    }

    public static void printDimensionInfo(Registry<LevelStem> dimensionRegistry) {
        if (!Configs.MAIN.verboseLogging.get()) return;

        StringBuilder output = new StringBuilder("World Dimensions: ");
        for (var entry : dimensionRegistry.entrySet()) {
            output.append("\n - ").append(entry.getKey().location()).append(": ")
                  .append("\n     ").append(entry.getValue().generator()).append(" ")
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
