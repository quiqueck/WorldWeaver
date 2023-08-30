package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.config.api.DatapackConfigs;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import org.betterx.wover.util.ResourceLocationSet;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

public class BiomeSourceManagerImpl {


    public static final ResourceLocation BIOME_CONFIGS = WoverWorldGenerator.C.id("biome_config.json");
    public static final ResourceLocation MINECRAFT_WILDCARD
            = new ResourceLocationSet.WildcardResourceLocation(IntegrationCore.MINECRAFT);
    public static final String BIOME_EXCLUSION_TAG = "exclude";
    public static final String NO_FABRIC_REGISTER_TAG = "no_fabric_register";
    public static final String END_CATCH_ALL = "*:is_end";

    public static void register(ResourceLocation location, Codec<? extends BiomeSource> codec) {
        BuiltInRegistryManager.register(BuiltInRegistries.BIOME_SOURCE, location, codec);
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(WoverWorldGenerator.C.id("nether_biome_source"), WoverNetherBiomeSource.CODEC);
        register(LegacyHelper.BCLIB_CORE.id("nether_biome_source"), LegacyHelper.wrap(WoverNetherBiomeSource.CODEC));

        register(WoverWorldGenerator.C.id("end_biome_source"), WoverEndBiomeSource.CODEC);
        register(LegacyHelper.BCLIB_CORE.id("end_biome_source"), LegacyHelper.wrap(WoverEndBiomeSource.CODEC));

        WorldLifecycle.RESOURCES_LOADED.subscribe(BiomeSourceManagerImpl::onResourcesLoaded);
        WorldLifecycle.WORLD_REGISTRY_READY.subscribeReadOnly(BiomeSourceManagerImpl::registerBiomesWithFabric, 100);
    }

    private static float genChance(BiomeData data, float defaultChance) {
        return data instanceof WoverBiomeData woverData ? woverData.genChance : defaultChance;
    }

    //Will register Biomes with Fabric using associated BiomeTags
    private static void registerBiomesWithFabric(RegistryAccess registryAccess, OnRegistryReady.Stage stage) {
        if (stage == OnRegistryReady.Stage.FINAL) {
            StopWatch sw = StopWatch.createStarted();
            int biomesAdded = 0;
            final Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);
            final Registry<BiomeData> biomeDataRegistry = registryAccess.registryOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
            for (var holder : biomes.holders().filter(h -> h.unwrapKey().isPresent()).toList()) {
                final ResourceKey<Biome> biomeKey = holder.unwrapKey().get();
                if (!FABRIC_EXCLUDES.contains(biomeKey.location())) {
                    if (holder.is(BiomeTags.IS_NETHER)) {
                        final BiomeData biomeData = biomeDataRegistry.get(biomeKey.location());

                        if (biomeData != null) {
                            for (var param : biomeData.parameterPoints) {
                                if (!NetherBiomes.canGenerateInNether(biomeKey)) {
                                    WoverWorldGenerator.C.log.verbose("Adding Nether Biome to Fabric: " + biomeKey.location() + " (" + param + ")");
                                    NetherBiomes.addNetherBiome(biomeKey, param);
                                    biomesAdded++;
                                }
                            }
                        }
                    } else if (!TheEndBiomesHelper.canGenerateInEnd(biomeKey)) {
                        final BiomeData biomeData = biomeDataRegistry.get(biomeKey.location());
                        if (holder.is(CommonBiomeTags.IS_END_HIGHLAND)) {
                            if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                                WoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                                TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 1.0f));
                                biomesAdded++;
                            }
                        } else if (holder.is(CommonBiomeTags.IS_END_CENTER)) {
                            if (!TheEndBiomesHelper.canGenerateAsMainIslandBiome(biomeKey)) {
                                WoverWorldGenerator.C.log.verbose("Adding End Center Biome to Fabric: " + biomeKey.location());
                                TheEndBiomes.addMainIslandBiome(biomeKey, genChance(biomeData, 1.0f));
                                biomesAdded++;
                            }
                        } else if (holder.is(CommonBiomeTags.IS_SMALL_END_ISLAND)) {
                            if (!TheEndBiomesHelper.canGenerateAsSmallIslandsBiome(biomeKey)) {
                                WoverWorldGenerator.C.log.verbose("Adding Small End Island Biome to Fabric: " + biomeKey.location());
                                TheEndBiomes.addSmallIslandsBiome(biomeKey, genChance(biomeData, 1.0f));
                                biomesAdded++;
                            }
                        } else if (holder.is(CommonBiomeTags.IS_END_MIDLAND)) {
                            if (!TheEndBiomesHelper.canGenerateAsEndMidlands(biomeKey)) {
                                if (biomeData instanceof WoverBiomeData woverData && woverData.parent != null) {
                                    WoverWorldGenerator.C.log.verbose("Adding End Midland Biome to Fabric: " + biomeKey.location());
                                    TheEndBiomes.addMidlandsBiome(woverData.parent, biomeKey, woverData.genChance);
                                    biomesAdded++;
                                } else if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                                    WoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                                    TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 0.5f));
                                    biomesAdded++;
                                }
                            }
                        } else if (holder.is(CommonBiomeTags.IS_END_BARRENS)) {
                            if (!TheEndBiomesHelper.canGenerateAsEndBarrens(biomeKey)) {
                                if (biomeData instanceof WoverBiomeData woverData && woverData.parent != null) {
                                    WoverWorldGenerator.C.log.verbose("Adding End Barrens Biome to Fabric: " + biomeKey.location());
                                    TheEndBiomes.addBarrensBiome(woverData.parent, biomeKey, woverData.genChance);
                                    biomesAdded++;
                                } else if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                                    WoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                                    TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 0.33f));
                                    biomesAdded++;
                                }
                            }
                        } else if (holder.is(BiomeTags.IS_END)) {
                            if (!TheEndBiomesHelper.canGenerateAsHighlandsBiome(biomeKey)) {
                                WoverWorldGenerator.C.log.verbose("Adding End Highland Biome to Fabric: " + biomeKey.location());
                                TheEndBiomes.addHighlandsBiome(biomeKey, genChance(biomeData, 1.0f));
                                biomesAdded++;
                            }
                        }
                    }
                }
            }

            if (biomesAdded > 0) {
                WoverWorldGenerator.C.log.info("Registered {} Biomes with Fabric in {}ms", biomesAdded, sw);
            }
        }
    }

    private static final Map<TagKey<Biome>, Set<ResourceLocation>> EXCLUSIONS = new HashMap<>();
    private static final Set<ResourceLocation> FABRIC_EXCLUDES = new ResourceLocationSet();

    public static void onResourcesLoaded(ResourceManager resourceManager) {
        EXCLUSIONS.clear();
        FABRIC_EXCLUDES.clear();

        //ensure vanilla biomes will net be registered with fabric
        FABRIC_EXCLUDES.add(MINECRAFT_WILDCARD);

        DatapackConfigs
                .instance()
                .runForResources(resourceManager, BIOME_CONFIGS, BiomeSourceManagerImpl::processBiomeConfigs);

        if (WorldState.registryAccess() != null && !EXCLUSIONS.isEmpty()) {
            WorldState.registryAccess()
                      .registry(Registries.LEVEL_STEM)
                      .ifPresent(levelStems -> levelStems.holders().forEach(holder -> {
                          if (holder.isBound()
                                  && holder.value().generator().getBiomeSource() instanceof ReloadableBiomeSource bs
                          ) {
                              bs.reloadBiomes();
                          }
                      }));
        }
    }


    public static Set<ResourceLocation> getExcludedBiomes(TagKey<Biome> tag) {
        return EXCLUSIONS.getOrDefault(tag, Set.of());
    }

    private static void addAllExclusions(List<TagKey<Biome>> tags, ResourceLocation biome) {
        tags.forEach(tag -> EXCLUSIONS.computeIfAbsent(tag, k -> new ResourceLocationSet()).add(biome));
    }

    private static void addBiomesToExclusion(
            JsonElement value,
            Consumer<ResourceLocation> adder
    ) {
        if (value.isJsonPrimitive()) {
            adder.accept(ResourceLocationSet.WildcardResourceLocation.parse(value.getAsString()));
        } else if (value.isJsonArray()) {
            value.getAsJsonArray()
                 .forEach(v -> adder.accept(ResourceLocationSet.WildcardResourceLocation.parse(v.getAsString())));
        }
    }

    private static void processBiomeConfigs(ResourceLocation location, JsonObject root) {
        if (root.has(BIOME_EXCLUSION_TAG)) {
            final JsonObject excludes = root.getAsJsonObject(BIOME_EXCLUSION_TAG);
            excludes.asMap().forEach((key, value) -> {
                if (key.equals(END_CATCH_ALL)) {
                    final List<TagKey<Biome>> endTags = WoverEndBiomeSource.TAGS;
                    addBiomesToExclusion(value, id -> addAllExclusions(endTags, id));
                } else {
                    final TagKey<Biome> tag = TagKey.create(Registries.BIOME, new ResourceLocation(key));
                    final Set<ResourceLocation> elements = EXCLUSIONS.computeIfAbsent(
                            tag,
                            k -> new ResourceLocationSet()
                    );
                    addBiomesToExclusion(value, elements::add);
                }
            });
        }

        if (root.has(NO_FABRIC_REGISTER_TAG)) {
            final JsonArray excludes = root.getAsJsonArray(NO_FABRIC_REGISTER_TAG);
            excludes.forEach(v -> FABRIC_EXCLUDES.add(ResourceLocationSet.WildcardResourceLocation.parse(v.getAsString())));
        }
    }


}
