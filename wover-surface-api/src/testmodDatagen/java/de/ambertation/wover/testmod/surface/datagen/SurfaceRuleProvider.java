package de.ambertation.wover.testmod.surface.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.surface.api.*;
import de.ambertation.wover.surface.api.noise.NumericProviders;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSurface;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import java.util.List;

public class SurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-plains"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS_BELOW
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-plains-below"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_BEACH
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-beach"));

    public static final ResourceKey<AssignedSurfaceRule> TEST_DESERT
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-desert"));

    public static final ResourceKey<AssignedSurfaceRule> TEST_FLOWER_FORREST
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-flower-forrest"));

    public SurfaceRuleProvider(ModCore modCore) {
        super(
                modCore,
                "Test Surface Rules",
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY
        );
    }


    @Override
    protected void bootstrap(BootstrapContext<AssignedSurfaceRule> ctx) {
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
                .subsurface(Blocks.CONCRETE.pick(DyeColor.WHITE).defaultBlockState(), 5)
                .register(ctx, TEST_BEACH);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.DESERT)
                .rule(Rules.switchRules(
                        NumericProviders.randomInt(2),
                        List.of(
                                MaterialRules.state(Blocks.DEEPSLATE.defaultBlockState()),
                                MaterialRules.state(Blocks.BLACKSTONE.defaultBlockState())
                        )
                ))
                .register(ctx, TEST_DESERT);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.FLOWER_FOREST)
                .rule(MaterialRules.sequence(
                        MaterialRules.ifTrue(
                                // 26.3 removed SurfaceRules.ON_FLOOR; the equivalent condition now lives
                                // in the minecraft:worldgen/material_condition datapack registry as
                                // VanillaMaterialConditions.ON_FLOOR. This inline form is the exact same
                                // value (see data/minecraft/worldgen/material_condition/on_floor.json).
                                MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR),
                                MaterialRules.ifTrue(
                                        Conditions.roughNoise(Noises.NETHERRACK, 0.19),
                                        MaterialRules.state(Blocks.CONCRETE.pick(DyeColor.PURPLE).defaultBlockState())
                                )
                        ),
                        MaterialRules.ifTrue(
                                Conditions.NETHER_VOLUME_NOISE_LARGE,
                                MaterialRules.state(Blocks.CONCRETE.pick(DyeColor.LIGHT_BLUE).defaultBlockState())
                        ),
                        MaterialRules.state(Blocks.CONCRETE.pick(DyeColor.WHITE).defaultBlockState())
                ))
                .register(ctx, TEST_FLOWER_FORREST);
    }
}
