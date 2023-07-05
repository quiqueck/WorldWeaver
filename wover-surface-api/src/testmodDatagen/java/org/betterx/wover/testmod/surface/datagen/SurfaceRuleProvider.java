package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.testmod.entrypoint.WoverSurfaceTestMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class SurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-plains"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS_BELOW
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-plains-below"));
    public static final ResourceKey<AssignedSurfaceRule> TEST_BEACH
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-beach"));

    public SurfaceRuleProvider() {
        super(
                WoverSurfaceTestMod.C,
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
                .register(ctx, TEST_PLAINS, 1001);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.PLAINS)
                .belowFloor(Blocks.OAK_PLANKS.defaultBlockState(), 2)
                .register(ctx, TEST_PLAINS_BELOW, 500);

        SurfaceRuleBuilder
                .start()
                .biome(Biomes.BEACH)
                .surface(Blocks.CHERRY_PLANKS.defaultBlockState())
                .subsurface(Blocks.WHITE_CONCRETE.defaultBlockState(), 5)
                .register(ctx, TEST_BEACH);
    }
}
