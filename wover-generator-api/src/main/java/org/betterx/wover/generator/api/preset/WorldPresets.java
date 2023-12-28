package org.betterx.wover.generator.api.preset;

import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.impl.preset.PresetRegistryImpl;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class WorldPresets {
    public final static ResourceKey<WorldPreset> WOVER_WORLD = PresetRegistryImpl.WOVER_WORLD;
    public final static ResourceKey<WorldPreset> WOVER_WORLD_LARGE = PresetRegistryImpl.WOVER_WORLD_LARGE;
    public final static ResourceKey<WorldPreset> WOVER_WORLD_AMPLIFIED = PresetRegistryImpl.WOVER_WORLD_AMPLIFIED;


    public static LevelStem makeWoverNetherStem(
            WorldPresetBootstrapContext.StemContext context,
            WoverNetherConfig config
    ) {
        return PresetRegistryImpl.makeWoverNetherStem(context, config);
    }

    public static LevelStem makeWoverEndStem(WorldPresetBootstrapContext.StemContext context, WoverEndConfig config) {
        return PresetRegistryImpl.makeWoverEndStem(context, config);
    }
}
