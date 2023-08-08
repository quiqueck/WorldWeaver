package org.betterx.wover.testmod.feature.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.testmod.entrypoint.WoverFeatureTestMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatureProvider extends WoverRegistryContentProvider<PlacedFeature> {
    public PlacedFeatureProvider(
            ModCore modCore
    ) {
        super(modCore, "Placed Features", Registries.PLACED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstapContext<PlacedFeature> context) {
        WoverFeatureTestMod.PLACED_REDSTONE_BLOCK.place(context)
                                                 .count(64)
                                                 .squarePlacement()
                                                 .onlyInBiome()
                                                 .heightmap()
                                                 .register();

        WoverFeatureTestMod.VANILLA_FEATURE.place(context)
                                           .squarePlacement()
                                           .onlyInBiome()
                                           .register();


        var cfg = ConfiguredFeatureManager.INLINE_BUILDER.simple().block(Blocks.QUARTZ_BLOCK).directHolder();


        WoverFeatureTestMod.INLINE_FEATURE.place(context, cfg)
                                          .squarePlacement()
                                          .onlyInBiome()
                                          .register();

        WoverFeatureTestMod.INLINE_FEATURE_ALL.inlineConfiguration(context)
                                              .simple()
                                              .block(Blocks.COAL_BLOCK)
                                              .inlinePlace()
                                              .isEmpty()
                                              .inRandomPatch()
                                              .inlinePlace()
                                              .squarePlacement()
                                              .onlyInBiome()
                                              .register();
    }
}
