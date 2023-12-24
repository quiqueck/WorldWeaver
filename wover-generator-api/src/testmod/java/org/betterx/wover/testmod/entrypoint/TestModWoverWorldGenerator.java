package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.BiomeManager;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.NetherForrestVegetation;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;
import org.betterx.wover.generator.api.biomesource.WoverBiomeBuilder;
import org.betterx.wover.generator.api.preset.PresetsRegistry;
import org.betterx.wover.preset.api.WorldPresetManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

import java.util.HashMap;
import java.util.Map;

public class TestModWoverWorldGenerator implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-generator-testmod");

    public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_TEST_BIOME
            = WoverBiomeBuilder.biomeKey(C.id("nether_test_biome"));

    public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_MAIN_BIOME = WoverBiomeBuilder.biomeKey(C.id(
            "nether_main_biome"));
    public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_SUB_BIOME = WoverBiomeBuilder.biomeKey(C.id(
            "nether_sub_biome"));
    public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_WASTE_SUB_BIOME = WoverBiomeBuilder.biomeKey(C.id(
            "wastes_sub_biome"));

    public static final ConfiguredFeatureKey<NetherForrestVegetation> TEST_VEGETATION
            = ConfiguredFeatureManager.netherForrestVegetation(C.id("test_vegetation"));

    public static final PlacedConfiguredFeatureKey TEST_VEGETATION_PLACED
            = PlacedFeatureManager.createKey(TEST_VEGETATION)
                                  .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);

    public static final PlacedFeatureKey TEST_SCATTERED_PLACED
            = PlacedFeatureManager
            .createKey(C.id("test_scattered_placed"))
            .setDecoration(GenerationStep.Decoration.SURFACE_STRUCTURES);

    @Override
    public void onInitialize() {
        WorldPresetManager.suggestDefault(PresetsRegistry.WOVER_WORLD, 2000);

        Map<ResourceKey<Biome>, Integer> testMap = new HashMap<>();
        ResourceKey<Biome> k2 = ResourceKey.create(
                Registries.BIOME,
                new ResourceLocation(Biomes.NETHER_WASTES.location().toString())
        );
        ResourceKey<Biome> k3 = ResourceKey.create(
                Registries.BIOME,
                new ResourceLocation(Biomes.NETHER_WASTES.location().toString())
        );
        testMap.put(Biomes.NETHER_WASTES, 10);
        testMap.put(k2, 20);
        if (testMap.containsKey(k3)) {
            C.log.info("testMap contains NETHER_WASTES");
        }

        C.log.info("" + testMap.get(k3));

        final BiomeKey<BiomeBuilder.Vanilla> RUNTIME_TEST_BIOME = BiomeManager.vanilla(C.id(
                "runtime_test_biome"));

        BiomeManager.BOOTSTRAP_BIOMES_WITH_DATA.subscribe(context -> {
            RUNTIME_TEST_BIOME
                    .bootstrap(context)
                    .surface(Blocks.AMETHYST_BLOCK)
                    .fogDensity(2.0F)
                    .isNetherBiome()
                    .hasPrecipitation(false)
                    .structure(BiomeTags.HAS_RUINED_PORTAL_NETHER)
                    .register();
        });
    }
}