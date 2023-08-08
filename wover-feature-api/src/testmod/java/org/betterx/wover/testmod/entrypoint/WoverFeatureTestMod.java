package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.ForSimpleBlock;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.api.ModInitializer;

public class WoverFeatureTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-feature-testmod");

    public static final ConfiguredFeatureKey<ForSimpleBlock>
            TEST_FEATURE_SIMPLE = ConfiguredFeatureManager.simple(C.id("test_feature"));
    public static final ConfiguredFeatureKey<ForSimpleBlock>
            CONFIGURED_REDSTONE_BLOCK = ConfiguredFeatureManager.simple(C.id("test_datagen"));

    public static final ConfiguredFeatureKey<ForSimpleBlock>
            TEST_RANDOM_SIMPLE = ConfiguredFeatureManager.simple(C.id("random_spread"));

    public static final PlacedFeatureKey INLINE_FEATURE = PlacedFeatureManager.createKey(C.id("inline_feature"));
    public static final PlacedFeatureKey INLINE_FEATURE_ALL = PlacedFeatureManager.createKey(C.id("inline_feature_all"));

    public static final PlacedFeatureKey.WithConfigured VANILLA_FEATURE = PlacedFeatureManager.createKey(
            C.id("vanilla_feature"),
            MiscOverworldFeatures.BLUE_ICE
    );
    public static final PlacedFeatureKey.WithConfigured PLACED_REDSTONE_BLOCK = PlacedFeatureManager.createKey(
            CONFIGURED_REDSTONE_BLOCK
    );

    public static final PlacedFeatureKey.WithConfigured PLACED_LAPIS_BLOCK = PlacedFeatureManager.createKey(
            TEST_FEATURE_SIMPLE
    );

    @Override
    public void onInitialize() {
        final var PLATEAU = PlacedFeatureManager.createKey(C.id("plateau"));

        ConfiguredFeatureManager.BOOTSTRAP_CONFIGURED_FEATURES.subscribe(ctx -> TEST_FEATURE_SIMPLE
                .bootstrap(ctx)
                .block(Blocks.LAPIS_BLOCK)
                .register());

        PlacedFeatureManager.BOOTSTRAP_PLACED_FEATURES.subscribe(ctx -> {
            PLATEAU.inlineConfiguration(ctx)
                   .simple()
                   .block(Blocks.GLASS)
                   .inlinePlace()
                   .squarePlacement()
                   .modifier(PlacementUtils.HEIGHTMAP)
                   .offset(new Vec3i(0, 8, 0))
                   .extendXZ(6, 1, 0.2f, false)
                   .register();

            PLACED_LAPIS_BLOCK
                    .place(ctx)
                    .count(16)
                    .squarePlacement()
                    .modifier(PlacementUtils.HEIGHTMAP)
                    .register();
        });

        BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.subscribe(ctx -> {
            BiomeModification
                    .build(ctx, C.id("lapis_modification"))
                    .isBiome(Biomes.SAVANNA)
                    .addFeature(PLACED_LAPIS_BLOCK)
                    .register();

            BiomeModification
                    .build(ctx, C.id("plateau_modification"))
                    .isBiome(Biomes.BADLANDS)
                    .addFeature(PLATEAU)
                    .register();
        });
    }

}