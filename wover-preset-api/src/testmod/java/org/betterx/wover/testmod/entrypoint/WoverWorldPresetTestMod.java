package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.WoverWorldPreset;
import org.betterx.wover.preset.api.SortableWorldPreset;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.WorldPresetTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.api.ModInitializer;

public class WoverWorldPresetTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like otehr Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-preset-testmod");

    public static final ResourceKey<WorldPreset> END_START
            = WorldPresetManager.createKey(C.id("end_start"));
    public static final ResourceKey<WorldPreset> NETHER_START =
            WorldPresetManager.createKey(WoverWorldPreset.C.id("nether_start"));

    @Override
    public void onInitialize() {
        WorldPresetManager.BOOTSTRAP_WORLD_PRESETS.subscribe(
                ctx -> {
                    var preset = SortableWorldPreset.fromStems(ctx.endStem, ctx.overworldStem, ctx.netherStem);
                    ctx.register(END_START, preset);
                }
        );

        WorldPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
            ctx.add(WorldPresetTags.NORMAL, END_START);
        });

        WorldPresetManager.suggestDefault(END_START, 1000);
        WorldPresetManager.suggestDefault(NETHER_START, 100);
    }
}