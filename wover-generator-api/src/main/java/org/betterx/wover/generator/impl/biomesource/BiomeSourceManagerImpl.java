package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.config.api.DatapackConfigs;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.state.api.WorldState;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;

public class BiomeSourceManagerImpl {
    public static final ResourceLocation BIOME_CONFIGS = WoverWorldGenerator.C.id("biome_config.json");
    public static final String BIOME_EXCLUSION_TAG = "exclude";

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
    }

    private static final Map<TagKey<Biome>, Set<ResourceLocation>> EXCLUSIONS = new HashMap<>();

    public static void onResourcesLoaded(ResourceManager resourceManager) {
        EXCLUSIONS.clear();

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

    private static void processBiomeConfigs(ResourceLocation location, JsonObject root) {
        if (root.has(BIOME_EXCLUSION_TAG)) {
            final JsonObject excludes = root.getAsJsonObject(BIOME_EXCLUSION_TAG);
            excludes.asMap().forEach((key, value) -> {
                final TagKey<Biome> tag = TagKey.create(Registries.BIOME, new ResourceLocation(key));
                final Set<ResourceLocation> elements = EXCLUSIONS.computeIfAbsent(tag, k -> new HashSet<>());
                if (value.isJsonPrimitive()) {
                    elements.add(new ResourceLocation(value.getAsString()));
                } else if (value.isJsonArray()) {
                    value.getAsJsonArray().forEach(v -> elements.add(new ResourceLocation(v.getAsString())));
                }
            });
        }
    }
}
