package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.testmod.entrypoint.WoverSurfaceTestMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class AddonSurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_MEADOW
            = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-meadow"));

    public AddonSurfaceRuleProvider(ModCore modCore) {
        super(
                modCore,
                "Additional Surface Rules",
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY
        );
    }


    @Override
    protected void bootstrap(BootstapContext<AssignedSurfaceRule> ctx) {
        SurfaceRuleBuilder
                .start()
                .biome(Biomes.MEADOW)
                .surface(Blocks.LIME_CONCRETE.defaultBlockState())
                .steep(Blocks.ORANGE_CONCRETE.defaultBlockState(), 3)
                .register(ctx, TEST_MEADOW);
    }
}