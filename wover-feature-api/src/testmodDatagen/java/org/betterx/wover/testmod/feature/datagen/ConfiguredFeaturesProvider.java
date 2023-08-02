package org.betterx.wover.testmod.feature.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.testmod.entrypoint.WoverFeatureTestMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
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

//        var rnd = ConfiguredFeatureManager.simple()
//                                          .block(Blocks.AMETHYST_BLOCK)
//                                          .inlinePlace()
//                                          .isEmpty()
//                                          .inRandomPatch()
//                                          .register(context);
    }
}
