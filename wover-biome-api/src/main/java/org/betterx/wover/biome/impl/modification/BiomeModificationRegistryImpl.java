package org.betterx.wover.biome.impl.modification;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.google.common.base.Stopwatch;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class BiomeModificationRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<BiomeModification>> BOOTSTRAP_BIOME_MODIFICATION_REGISTRY
            = new EventImpl<>("BOOTSTRAP_BIOME_MODIFICATION_REGISTRY");

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.register(
                BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY,
                BiomeModification.CODEC,
                BiomeModificationRegistryImpl::onBootstrap
        );

        WorldLifecycle.WORLD_REGISTRY_READY.subscribe(BiomeModificationRegistryImpl::setCurrentRegistryAccess);
        WorldLifecycle.MINECRAFT_SERVER_READY.subscribe(BiomeModificationRegistryImpl::whenReady);
        TagManager.BIOMES.bootstrapEvent().subscribe(BiomeModificationRegistryImpl::addBiomeTags);
    }

    private static RegistryAccess lastRegistryAccess;

    private static void setCurrentRegistryAccess(RegistryAccess registryAccess, OnRegistryReady.Stage stage) {
        lastRegistryAccess = registryAccess;
    }

    private static void addBiomeTags(TagBootstrapContext<Biome> biomeTagBootstrapContext) {
        final Stopwatch sw = Stopwatch.createStarted();

        final RegistryAccess registryAccess = lastRegistryAccess;
        if (registryAccess == null) {
            WoverBiome.C.log.warn("Failed to apply biome tag modifications. Registry access is null.");
            return;
        }
        final Registry<BiomeModification> modifications = registryAccess.registryOrThrow(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
        final Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);
        final List<BiomeModification> biomeModifications = modifications.stream().toList();

        int biomesProcessed = 0;
        int modifiersApplied = 0;

        final List<ResourceKey<Biome>> keys = biomes
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(key -> biomes.getId(biomes.getOrThrow(key))))
                .toList();

        for (ResourceKey<Biome> biomeKey : keys) {
            final BiomePredicate.Context context = BiomePredicate.Context.of(
                    WorldState.registryAccess(),
                    biomes,
                    biomeKey
            );
            if (context == null) {
                WoverBiome.C.log.warn("Failed to get biome context for {}", biomeKey.location());
                continue;
            }
            boolean didUpdate = false;

            for (BiomeModification modification : biomeModifications) {
                if (!modification.biomeTags().isEmpty() && modification.predicate().test(context)) {
                    modifiersApplied++;
                    for (TagKey<Biome> tag : modification.biomeTags().orElse(List.of())) {
                        biomeTagBootstrapContext.add(tag, context.biome);
                        didUpdate = true;
                    }
                }
            }
            if (didUpdate) {
                biomesProcessed++;
            }
        }

        if (biomesProcessed > 0) {
            WoverBiome.C.log.info(
                    "Applied {} biome tag extensions to {} biomes in {}",
                    modifiersApplied,
                    biomesProcessed,
                    sw.stop()
            );
        }
    }

    private static void onBootstrap(BootstapContext<BiomeModification> ctx) {
        BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    // based on Fabrics net.fabricmc.fabric.api.biome.v1.BiomeModifications
    // We need to reimplement this, as we want to drive the Modifications
    // from a Datapack backed Registry.
    // The current Fabric API implementation is not suitable for this.
    private static void whenReady(
            LevelStorageSource.LevelStorageAccess storageSource,
            PackRepository packRepository,
            WorldStem worldStem
    ) {
        final Stopwatch sw = Stopwatch.createStarted();

        final RegistryAccess registryAccess = WorldState.registryAccess();
        final Registry<BiomeModification> modifications = registryAccess.registryOrThrow(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
        final Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);

        final List<ResourceKey<Biome>> keys = biomes
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparingInt(key -> biomes.getId(biomes.getOrThrow(key))))
                .toList();

        final List<BiomeModification> biomeModifications = modifications.stream().toList();

        int biomesChanged = 0;
        int biomesProcessed = 0;
        int modifiersApplied = 0;

        for (ResourceKey<Biome> biomeKey : keys) {
            BiomePredicate.Context context = BiomePredicate.Context.of(registryAccess, biomeKey);
            if (context == null) {
                WoverBiome.C.log.warn("Failed to get biome context for {}", biomeKey.location());
                continue;
            }

            biomesProcessed++;
            GenerationSettingsWorker worker = null;

            for (BiomeModification modification : biomeModifications) {
                if (modification.predicate().test(context)) {
                    if (worker == null) {
                        worker = new GenerationSettingsWorker(registryAccess, context.biome);
                    }

                    modification.apply(worker);
                    modifiersApplied++;
                }
            }

            if (worker != null) {
                if (worker.finished()) {
                    biomesChanged++;
                }
            }
        }

        if (biomesProcessed > 0) {
            WoverBiome.C.log.info(
                    "Applied {} biome modifications to {} of {} biomes in {}",
                    modifiersApplied,
                    biomesChanged,
                    biomesProcessed,
                    sw.stop()
            );
        }
    }

    public static ResourceKey<BiomeModification> createKey(
            ResourceLocation modificationID
    ) {
        return ResourceKey.create(
                BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY,
                modificationID
        );
    }
}
