package de.ambertation.wover.generator.impl.chunkgenerator;

import de.ambertation.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.state.api.WorldState;

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
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;

public class WoverChunkGeneratorImpl {
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
                                                                     .lookupOrThrow(Registries.LEVEL_STEM);
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
        for (var entry : WorldState.registryAccess().lookupOrThrow(Registries.LEVEL_STEM).entrySet()) {
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
        final Registry<LevelStem> dimensions = access.lookupOrThrow(Registries.LEVEL_STEM);

        final BiomeRepairHelper biomeHelper = new BiomeRepairHelper();
        final Registry<LevelStem> changedDimensions = biomeHelper.repairBiomeSourceInAllDimensions(access, dimensions);

        if (dimensions != changedDimensions) {
            LibWoverWorldGenerator.C.log.verbose("Loading World with initially configured Dimensions.");
            registries = registries.replaceFrom(
                    RegistryLayer.DIMENSIONS,
                    new RegistryAccess.ImmutableRegistryAccess(List.of(changedDimensions)).freeze()
            );
        }

        return registries;
    }

    public interface RegisterHelper {
        Holder.Reference<LevelStem> register(
                MappedRegistry<LevelStem> writableRegistry,
                ResourceKey<LevelStem> key,
                LevelStem stem
        );
    }

    public interface StemGetter {
        LevelStem get(ResourceKey<LevelStem> key);
    }

    public static Registry<LevelStem> replaceGenerator(
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            RegistryAccess registryAccess,
            Set<Map.Entry<ResourceKey<LevelStem>, LevelStem>> dimensionRegistry,
            ChunkGenerator generator,
            StemGetter getter,
            RegisterHelper registerHelper
    ) {
        final Registry<DimensionType> dimensionTypeRegistry = registryAccess.lookupOrThrow(Registries.DIMENSION_TYPE);
        final LevelStem levelStem = getter.get(dimensionKey);

        Holder<DimensionType> dimensionType = levelStem == null
                ? dimensionTypeRegistry.getOrThrow(dimensionTypeKey)
                : levelStem.type();

        MappedRegistry<LevelStem> writableRegistry = new MappedRegistry<>(
                Registries.LEVEL_STEM,
                Lifecycle.experimental()
        );

        writableRegistry.register(
                dimensionKey,
                new LevelStem(dimensionType, generator),
                RegistrationInfo.BUILT_IN
        );

        //copy all other dimensions
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensionRegistry) {
            final ResourceKey<LevelStem> resourceKey = entry.getKey();
            if (dimensionKey.identifier().equals(resourceKey.identifier())) continue;

            registerHelper.register(writableRegistry, resourceKey, entry.getValue());
        }

        return writableRegistry;
    }

}
