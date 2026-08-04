package de.ambertation.wover.testmod.feature.datagen;

import de.ambertation.wover.block.api.predicate.BlockPredicates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;
import de.ambertation.wover.feature.api.features.config.PillarFeatureConfig;
import de.ambertation.wover.testmod.entrypoint.TestModWoverFeature;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
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
    protected void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TestModWoverFeature.CONFIGURED_REDSTONE_BLOCK
                .bootstrap(context)
                .block(Blocks.REDSTONE_BLOCK)
                .register();

        TestModWoverFeature.TEST_RANDOM_SIMPLE
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

        ConfiguredFeatureManager
                .pillar(modCore.id("patch_basalt_stalactite"))
                .bootstrap(context)
                .transformer(PillarFeatureConfig.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(Blocks.SMOOTH_BASALT)
                .maxHeight(BiasedToBottomInt.of(4, 11))
                .inlinePlace()
                .isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
                .inRandomPatch()
                .register();
    }
}
