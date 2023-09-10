package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.api.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldState;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class WoverChunkGeneratorImpl {
    public static final ResourceKey<NoiseGeneratorSettings> LEGACY_AMPLIFIED_NETHER = ResourceKey.create(
            Registries.NOISE_SETTINGS,
            LegacyHelper.BCLIB_CORE.convertNamespace(WoverChunkGenerator.AMPLIFIED_NETHER.location())
    );

    @ApiStatus.Internal
    public static void initialize() {
        WorldLifecycle.MINECRAFT_SERVER_READY.subscribe(WoverChunkGeneratorImpl::restoreInitialBiomeSourceInAllDimensions);
        WorldLifecycle.ON_DIMENSION_LOAD.subscribe(WoverChunkGeneratorImpl::repairBiomeSourceInAllDimensions);
        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(WoverChunkGeneratorImpl::printInfo, -1000);
    }

    private static void printInfo(
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            LayeredRegistryAccess<RegistryLayer> registryLayerLayeredRegistryAccess,
            WorldData worldData
    ) {
        if (WorldState.registryAccess() != null) {
            final Registry<LevelStem> dimensionsRegistry = WorldState.registryAccess()
                                                                     .registryOrThrow(Registries.LEVEL_STEM);
            ChunkGeneratorManagerImpl.printDimensionInfo(dimensionsRegistry);
        }
    }


    /**
     * Some mods forcefully swap the biomeSource that is attached to a ChunkGenerator. This method checks if the
     * Generator implements {@link RestorableBiomeSource}, and if so, it will restore the original biomeSource, usually
     * the one that was created in the constructor of the generator.
     *
     * @param levelStorageAccess The levelStorageAccess
     * @param packRepository     The packRepository
     * @param worldStem          The worldStem
     */
    private static void restoreInitialBiomeSourceInAllDimensions(
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            WorldStem worldStem
    ) {
        for (var entry : WorldState.registryAccess().registryOrThrow(Registries.LEVEL_STEM).entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();

            if (stem.generator() instanceof RestorableBiomeSource<?> generator) {
                generator.restoreInitialBiomeSource(key);
            }
        }
    }

    private static LayeredRegistryAccess<RegistryLayer> repairBiomeSourceInAllDimensions(LayeredRegistryAccess<RegistryLayer> registries) {
        WorldGeneratorConfigImpl.migrateGeneratorSettings();

        final RegistryAccess.Frozen access = registries.compositeAccess();
        final Registry<LevelStem> dimensions = access.registryOrThrow(Registries.LEVEL_STEM);

        final BiomeRepairHelper biomeHelper = new BiomeRepairHelper();
        final Registry<LevelStem> changedDimensions = biomeHelper.repairBiomeSourceInAllDimensions(access, dimensions);

        if (dimensions != changedDimensions) {
            WoverWorldGenerator.C.log.verbose("Loading World with initially configured Dimensions.");
            registries = registries.replaceFrom(
                    RegistryLayer.DIMENSIONS,
                    new RegistryAccess.ImmutableRegistryAccess(List.of(changedDimensions)).freeze()
            );
        }

        return registries;
    }

    public static Registry<LevelStem> replaceGenerator(
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            RegistryAccess registryAccess,
            Registry<LevelStem> dimensionRegistry,
            ChunkGenerator generator
    ) {
        final Registry<DimensionType> dimensionTypeRegistry = registryAccess.registryOrThrow(Registries.DIMENSION_TYPE);
        final LevelStem levelStem = dimensionRegistry.get(dimensionKey);

        Holder<DimensionType> dimensionType = levelStem == null
                ? dimensionTypeRegistry.getHolderOrThrow(dimensionTypeKey)
                : levelStem.type();

        MappedRegistry<LevelStem> writableRegistry = new MappedRegistry<>(
                Registries.LEVEL_STEM,
                Lifecycle.experimental()
        );

        writableRegistry.register(
                dimensionKey,
                new LevelStem(dimensionType, generator),
                Lifecycle.stable()
        );

        //copy all other dimensions
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensionRegistry.entrySet()) {
            ResourceKey<LevelStem> resourceKey = entry.getKey();
            if (dimensionKey.location().equals(resourceKey.location())) continue;

            writableRegistry.register(
                    resourceKey,
                    entry.getValue(),
                    dimensionRegistry.lifecycle(entry.getValue())
            );
        }

        return writableRegistry;
    }

}
