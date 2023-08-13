package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.BiomeManager;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

public class WoverBiomeTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-biome-testmod");

    public static final BiomeKey<BiomeBuilder.Vanilla> TEST_BIOME = BiomeManager.vanilla(C.id("test_biome"));

    @Override
    public void onInitialize() {

        BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.subscribe(context -> {
            var features = context.lookup(Registries.PLACED_FEATURE);

            BiomeModification
                    .build(context, C.id("runtime_modification"))
                    .isBiome(Biomes.MEADOW)
                    .addFeature(
                            GenerationStep.Decoration.VEGETAL_DECORATION,
                            features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
                    )
                    .register();
        });
    }
}