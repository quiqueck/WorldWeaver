package org.betterx.wover.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.generator.api.preset.WorldPresets;
import org.betterx.wover.preset.api.WorldPresetInfo;
import org.betterx.wover.preset.api.WorldPresetInfoBuilder;
import org.betterx.wover.preset.api.WorldPresetInfoRegistry;

import net.minecraft.data.worldgen.BootstapContext;

public class WorldPresetInfoProvider extends WoverRegistryContentProvider<WorldPresetInfo> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WorldPresetInfoProvider(
            ModCore modCore
    ) {
        super(modCore, "World Preset Info", WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstapContext<WorldPresetInfo> context) {
        WorldPresetInfoBuilder.start(context)
                              .order(1500)
                              .overworldOverride(net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL)
                              .register(WorldPresets.WOVER_WORLD);

        WorldPresetInfoBuilder.start(context)
                              .order(2500)
                              .overworldOverride(net.minecraft.world.level.levelgen.presets.WorldPresets.AMPLIFIED)
                              .endOverride(WorldPresets.WOVER_WORLD)
                              .register(WorldPresets.WOVER_WORLD_AMPLIFIED);

        WorldPresetInfoBuilder.start(context)
                              .order(3500)
                              .overworldOverride(net.minecraft.world.level.levelgen.presets.WorldPresets.LARGE_BIOMES)
                              .register(WorldPresets.WOVER_WORLD_LARGE);
    }
}
