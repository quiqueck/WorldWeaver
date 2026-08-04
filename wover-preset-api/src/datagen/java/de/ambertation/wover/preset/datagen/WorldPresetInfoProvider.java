package de.ambertation.wover.preset.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.preset.api.WorldPresetInfo;
import de.ambertation.wover.preset.api.WorldPresetInfoBuilder;
import de.ambertation.wover.preset.api.WorldPresetInfoRegistry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public class WorldPresetInfoProvider extends WoverRegistryContentProvider<WorldPresetInfo> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WorldPresetInfoProvider(
            ModCore modCore
    ) {
        super(modCore, "Vanilla World Preset Info", WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<WorldPresetInfo> context) {
        WorldPresetInfoBuilder.start(context)
                              .order(1000)
                              .register(WorldPresets.NORMAL);

        WorldPresetInfoBuilder.start(context)
                              .order(2000)
                              .endOverride(WorldPresets.NORMAL)
                              .netherOverride(WorldPresets.NORMAL)
                              .register(WorldPresets.AMPLIFIED);

        WorldPresetInfoBuilder.start(context)
                              .order(3000)
                              .register(WorldPresets.LARGE_BIOMES);

        WorldPresetInfoBuilder.start(context)
                              .order(11000)
                              .overworldOverride(WorldPresets.NORMAL)
                              .endOverride(WorldPresets.NORMAL)
                              .netherOverride(WorldPresets.NORMAL)
                              .register(WorldPresets.FLAT);

        WorldPresetInfoBuilder.start(context)
                              .order(12000)
                              .overworldOverride(WorldPresets.NORMAL)
                              .endOverride(WorldPresets.NORMAL)
                              .netherOverride(WorldPresets.NORMAL)
                              .register(WorldPresets.SINGLE_BIOME_SURFACE);
    }
}
