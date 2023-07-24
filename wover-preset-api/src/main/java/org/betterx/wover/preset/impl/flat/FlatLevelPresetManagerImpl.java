package org.betterx.wover.preset.impl.flat;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.preset.api.context.FlatLevelPresetBootstrapContext;
import org.betterx.wover.preset.api.event.OnBootstrapFlatLevelPresets;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

import org.jetbrains.annotations.ApiStatus;

public class FlatLevelPresetManagerImpl {
    public static final EventImpl<OnBootstrapFlatLevelPresets> BOOTSTRAP_FLAT_LEVEL_PRESETS = new EventImpl<>(
            "BOOTSTRAP_FLAT_LEVEL_PRESETS");

    public static ResourceKey<FlatLevelGeneratorPreset> createKey(ResourceLocation loc) {
        return ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, loc);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.FLAT_LEVEL_GENERATOR_PRESET,
                FlatLevelPresetManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstapContext<FlatLevelGeneratorPreset> context) {
        FlatLevelPresetBootstrapContext ctx = new FlatLevelPresetBootstrapContext(context);
        BOOTSTRAP_FLAT_LEVEL_PRESETS.emit(c -> c.bootstrap(ctx));
    }
}
