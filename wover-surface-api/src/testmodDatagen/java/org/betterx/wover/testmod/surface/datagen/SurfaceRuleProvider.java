package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.surface.api.*;
import org.betterx.wover.surface.api.noise.NumericProviders;
import org.betterx.wover.testmod.entrypoint.WoverSurfaceTestMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class SurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-plains"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS_BELOW
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-plains-below"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_BEACH
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-beach"));

    public static final ResourceKey<AssignedSurfaceRule> TEST_DESERT
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-desert"));

    public static final ResourceKey<AssignedSurfaceRule> TEST_FLOWER_FORREST
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-flower-forrest"));

    public SurfaceRuleProvider(ModCore modCore) {
        super(
                modCore,
                "Test Surface Rules",
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY
        );
    }


    @Override
    protected void bootstrap(BootstapContext<AssignedSurfaceRule> ctx) {
        SurfaceRuleBuilder
                .start()
                .biome(Biomes.PLAINS)
                .surface(Blocks.ACACIA_PLANKS.defaultBlockState())
                .sortPriority(1001)
                .register(ctx, TEST_PLAINS);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.PLAINS)
                .belowFloor(Blocks.OAK_PLANKS.defaultBlockState(), 2)
                .sortPriority(500)
                .register(ctx, TEST_PLAINS_BELOW);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.BEACH)
                .surface(Blocks.CHERRY_PLANKS.defaultBlockState())
                .subsurface(Blocks.WHITE_CONCRETE.defaultBlockState(), 5)
                .register(ctx, TEST_BEACH);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.DESERT)
                .rule(Rules.switchRules(
                        NumericProviders.randomInt(2),
                        List.of(
                                SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState()),
                                SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState())
                        )
                ))
                .register(ctx, TEST_DESERT);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.FLOWER_FOREST)
                .rule(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(
                                        Conditions.roughNoise(Noises.NETHERRACK, 0.19),
                                        SurfaceRules.state(Blocks.PURPLE_CONCRETE.defaultBlockState())
                                )
                        ),
                        SurfaceRules.ifTrue(
                                Conditions.NETHER_VOLUME_NOISE_LARGE,
                                SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState())
                        ),
                        SurfaceRules.state(Blocks.WHITE_CONCRETE.defaultBlockState())
                ))
                .register(ctx, TEST_FLOWER_FORREST);
    }
}
