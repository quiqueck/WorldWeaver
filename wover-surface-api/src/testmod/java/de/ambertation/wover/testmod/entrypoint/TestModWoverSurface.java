package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.util.PriorityLinkedList;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;

public class TestModWoverSurface implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-surface-testmod");
    public static final Identifier ADDON_PACK = C.addDatapack(
            "testpack",
            ResourcePackActivationType.DEFAULT_ENABLED
    );

    @Override
    public void onInitialize() {
        WorldLifecycle.WORLD_REGISTRY_READY.subscribe((registry, stage) -> {
            Registry<AssignedSurfaceRule> surfaceRegistr = registry.lookupOrThrow(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
            C.log.info("Surface rule registry: " + Integer.toHexString(surfaceRegistr.hashCode()));

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