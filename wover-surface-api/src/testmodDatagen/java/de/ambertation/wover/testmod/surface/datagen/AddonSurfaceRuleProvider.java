package de.ambertation.wover.testmod.surface.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSurface;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class AddonSurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_MEADOW
            = SurfaceRuleRegistry.createKey(TestModWoverSurface.C.id("test-meadow"));

    public AddonSurfaceRuleProvider(ModCore modCore) {
        super(
                modCore,
                "Additional Surface Rules",
                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY
        );
    }


    @Override
    protected void bootstrap(BootstrapContext<AssignedSurfaceRule> ctx) {
        SurfaceRuleBuilder
                .start()
                .biome(Biomes.MEADOW)
                .surface(Blocks.LIME_CONCRETE.defaultBlockState())
                .steep(Blocks.ORANGE_CONCRETE.defaultBlockState(), 3)
                .register(ctx, TEST_MEADOW);
    }
}