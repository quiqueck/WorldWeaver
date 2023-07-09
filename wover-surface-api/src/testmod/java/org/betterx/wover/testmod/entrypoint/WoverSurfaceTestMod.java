package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.surface.api.noise.NoiseParameterManager;
import org.betterx.wover.util.PriorityLinkedList;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;

public class WoverSurfaceTestMod implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-surface-testmod");
    public static final ResourceLocation ADDON_PACK = C.addDatapack(
            "testpack",
            ResourcePackActivationType.DEFAULT_ENABLED
    );

    @Override
    public void onInitialize() {
        RandomSource rSource = new SingleThreadedRandomSource(9324765982365L);
        WorldLifecycle.WORLD_REGISTRY_READY.subscribe((registry, stage) -> {
            Registry<AssignedSurfaceRule> surfaceRegistr = registry.registryOrThrow(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
            C.log.info("Surface rule registry: " + Integer.toHexString(surfaceRegistr.hashCode()));

            if (stage != OnRegistryReady.Stage.FINAL) return;
            var test = NoiseParameterManager.getOrCreateNoise(registry, rSource, NoiseParameterManager.ROUGHNESS_NOISE);
            System.out.println(test.getValue(1, 2, 3));
            System.out.println(test.getValue(1, 2, 3));
        });
        PriorityLinkedList<String> list = new PriorityLinkedList<>();
        list.add("b", 200);
        list.add("a", 100);
        list.add("c", 300);

        System.out.println(list);

        if (!ModCore.isDatagen()) {
            var TEST_SAVANA = SurfaceRuleRegistry.createKey(C.id("test-savana"));
            SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY.subscribe(ctx -> {
                SurfaceRuleBuilder
                        .start()
                        .biome(Biomes.SAVANNA)
                        .chancedFloor(
                                Blocks.RED_TERRACOTTA.defaultBlockState(),
                                Blocks.RED_CONCRETE.defaultBlockState()
                        )
                        .register(ctx, TEST_SAVANA);
            });
        }
    }
}