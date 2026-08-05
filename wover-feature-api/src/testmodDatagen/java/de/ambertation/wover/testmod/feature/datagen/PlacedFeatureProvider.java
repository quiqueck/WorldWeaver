package de.ambertation.wover.testmod.feature.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.testmod.entrypoint.TestModWoverFeature;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class PlacedFeatureProvider extends WoverRegistryContentProvider<PlacedFeature> {
    public PlacedFeatureProvider(
            ModCore modCore
    ) {
        super(modCore, "Placed Features", Registries.PLACED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstrapContext<PlacedFeature> context) {
        TestModWoverFeature.PLACED_REDSTONE_BLOCK.place(context)
                                                 .count(64)
                                                 .squarePlacement()
                                                 .onlyInBiome()
                                                 .heightmap()
                                                 .register();

        TestModWoverFeature.VANILLA_FEATURE.place(context)
                                           .squarePlacement()
                                           .onlyInBiome()
                                           .register();


        var cfg = ConfiguredFeatureManager.INLINE_BUILDER.simple().block(Blocks.QUARTZ_BLOCK).directHolder();


        TestModWoverFeature.INLINE_FEATURE.place(context, cfg)
                                          .squarePlacement()
                                          .onlyInBiome()
                                          .register();

        // Migrated from the deprecated random_patch shim (.isEmpty().inRandomPatch()...) to the
        // vanilla-migrated placement-based scatter API. This emits, in vanilla order:
        // in_square, count(96), random_offset(trapezoid ±7, trapezoid ±3), block_predicate_filter(air), biome.
        TestModWoverFeature.INLINE_FEATURE_ALL.inlineConfiguration(context)
                                              .simple()
                                              .block(Blocks.COAL_BLOCK)
                                              .inlinePlace()
                                              .squarePlacement()
                                              .scatter(96, 7, 3, BlockPredicate.ONLY_IN_AIR_PREDICATE)
                                              .register();
    }
}
