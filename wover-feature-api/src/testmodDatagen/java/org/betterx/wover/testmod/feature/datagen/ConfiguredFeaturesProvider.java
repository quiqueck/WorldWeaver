package org.betterx.wover.testmod.feature.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsPillar;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;
import org.betterx.wover.testmod.entrypoint.WoverFeatureTestMod;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ConfiguredFeaturesProvider extends WoverRegistryContentProvider<ConfiguredFeature<?, ?>> {
    public ConfiguredFeaturesProvider(
            ModCore modCore
    ) {
        super(modCore, "Configured Features", Registries.CONFIGURED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        WoverFeatureTestMod.CONFIGURED_REDSTONE_BLOCK
                .bootstrap(context)
                .block(Blocks.REDSTONE_BLOCK)
                .register();

        WoverFeatureTestMod.TEST_RANDOM_SIMPLE
                .bootstrap(context)
                .block(Blocks.AMETHYST_BLOCK)
                .inlinePlace()
                .isEmpty()
                .inRandomPatch()
                .register();

        ConfiguredFeatureKey<AsPillar> TEST_KEY = ConfiguredFeatureManager.pillar(
                modCore.id("test")
        );
        TEST_KEY.bootstrap(context)
                .allowedPlacement(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE)
                .maxHeight(UniformInt.of(4, 8))
                .minHeight(3)
                .direction(Direction.UP)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_INCREASE)
                .blockState(Blocks.LAPIS_BLOCK)
                .register();
//        var rnd = ConfiguredFeatureManager.simple()
//                                          .block(Blocks.AMETHYST_BLOCK)
//                                          .inlinePlace()
//                                          .isEmpty()
//                                          .inRandomPatch()
//                                          .register(context);
    }
}
