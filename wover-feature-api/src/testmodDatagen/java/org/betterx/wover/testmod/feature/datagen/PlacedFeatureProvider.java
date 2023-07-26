package org.betterx.wover.testmod.feature.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.testmod.entrypoint.WoverFeatureTestMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
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
                                                 .modifier(PlacementUtils.HEIGHTMAP)
                                                 .register(context);

        WoverFeatureTestMod.VANILLA_FEATURE.place(context)
                                           .squarePlacement()
                                           .register(context);


        var cfg = ConfiguredFeatureManager.INLINE_BUILDER.simple().block(Blocks.QUARTZ_BLOCK).directHolder();


        WoverFeatureTestMod.INLINE_FEATURE.place(cfg)
                                          .squarePlacement()
                                          .register(context);

        WoverFeatureTestMod.INLINE_FEATURE_2.inlineConfiguration()
                                            .simple()
                                            .block(Blocks.COAL_BLOCK)
                                            .inlinePlace()
                                            .isEmpty()
                                            .inRandomPatch()
                                            .inlinePlace()
                                            .squarePlacement()
                                            .register(context);
    }
}
