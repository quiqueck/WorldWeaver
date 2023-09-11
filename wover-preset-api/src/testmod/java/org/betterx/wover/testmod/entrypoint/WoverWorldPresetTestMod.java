package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.WoverWorldPreset;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.WorldPresetTags;
import org.betterx.wover.preset.api.flat.FlatLevelPresetManager;
import org.betterx.wover.preset.api.flat.FlatLevelPresetTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.api.ModInitializer;

import java.util.HashSet;

public class WoverWorldPresetTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-preset-testmod");

    public static final ResourceKey<WorldPreset> END_START
            = WorldPresetManager.createKey(C.id("end_start"));
    public static final ResourceKey<WorldPreset> NETHER_START =
            WorldPresetManager.createKey(WoverWorldPreset.C.id("nether_start"));
    public static final ResourceKey<FlatLevelGeneratorPreset> FLAT_NETHER =
            FlatLevelPresetManager.createKey(WoverWorldPreset.C.id("nether"));

    @Override
    public void onInitialize() {
        WorldPresetManager.BOOTSTRAP_WORLD_PRESETS.subscribe(
                ctx -> {
                    var preset = WorldPresetManager.fromStems(ctx.endStem, ctx.overworldStem, ctx.netherStem);
                    ctx.register(END_START, preset);
                }
        );

        WorldPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
            ctx.add(WorldPresetTags.NORMAL, END_START);
        });

        WorldPresetManager.suggestDefault(END_START, 1000);
        WorldPresetManager.suggestDefault(NETHER_START, 100);

        FlatLevelPresetManager.BOOTSTRAP_FLAT_LEVEL_PRESETS.subscribe(
                ctx -> {
                    ctx.register(
                            FLAT_NETHER,
                            Blocks.NETHER_BRICKS,
                            Biomes.NETHER_WASTES,
                            new HashSet<>(0),
                            false,
                            false,
                            new FlatLayerInfo(12, Blocks.NETHERRACK),
                            new FlatLayerInfo(2, Blocks.NETHER_BRICKS)
                    );
                }
        );

        FlatLevelPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
            ctx.add(FlatLevelPresetTags.VISIBLE, FLAT_NETHER);
        });
    }
}