package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.builders.ForSimpleBlock;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.api.placed.PlacedFeatureManager;

import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.api.ModInitializer;

public class WoverFeatureTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-feature-testmod");

    public static final ConfiguredFeatureKey<ForSimpleBlock>
            TEST_FEATURE_SIMPLE = ConfiguredFeatureManager.simple(C.id("test_feature"));
    public static final ConfiguredFeatureKey<ForSimpleBlock>
            TEST_DATAGEN_SIMPLE = ConfiguredFeatureManager.simple(C.id("test_datagen"));

    public static final ConfiguredFeatureKey<ForSimpleBlock>
            TEST_RANDOM_SIMPLE = ConfiguredFeatureManager.simple(C.id("random_spread"));

    public static final PlacedFeatureKey INLINE_FEATURE = PlacedFeatureManager.createKey(C.id("inline_feature"));
    public static final PlacedFeatureKey INLINE_FEATURE_2 = PlacedFeatureManager.createKey(C.id("inline_feature_2"));

    public static final PlacedFeatureKey.WithConfigured VANILLA_FEATURE = PlacedFeatureManager.createKey(
            C.id("vanilla_feature"),
            MiscOverworldFeatures.BLUE_ICE
    );
    public static final PlacedFeatureKey.WithConfigured REF_FEATURE = PlacedFeatureManager.createKey(
            TEST_DATAGEN_SIMPLE
    );

    @Override
    public void onInitialize() {
        ConfiguredFeatureManager.BOOTSTRAP_CONFIGURED_FEATURES.subscribe(ctx -> TEST_FEATURE_SIMPLE
                .bootstrap()
                .block(Blocks.LAPIS_BLOCK)
                .register(ctx));
    }

}