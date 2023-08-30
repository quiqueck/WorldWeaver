package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.biome.api.BiomeKey;
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

import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

public class WoverWorldGeneratorTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-generator-testmod");

    public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_TEST_BIOME
            = WoverBiomeBuilder.biomeKey(C.id("nether_test_biome"));
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
    }
}