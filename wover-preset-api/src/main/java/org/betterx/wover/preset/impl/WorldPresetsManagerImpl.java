package org.betterx.wover.preset.impl;

import org.betterx.wover.core.api.DatapackRegistryBuilder;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;
import org.betterx.wover.preset.api.event.OnBootstrapWorldPresets;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.ApiStatus;

public class WorldPresetsManagerImpl {
    public static final EventImpl<OnBootstrapWorldPresets> BOOTSTRAP_WORLD_PRESETS = new EventImpl<>(
            "BOOTSTRAP_WORLD_PRESETS");
    private static ResourceKey<WorldPreset> DEFAULT = net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL;
    private static int currentDefaultPriority = Integer.MIN_VALUE;

    public static Holder<WorldPreset> get(RegistryAccess access, ResourceKey<WorldPreset> key) {
        return access
                .registryOrThrow(Registries.WORLD_PRESET)
                .getHolderOrThrow(key);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.registerNotification(
                Registries.WORLD_PRESET,
                WorldPresetsManagerImpl::onBootstrap
        );
    }

    public static ResourceKey<WorldPreset> createKey(ResourceLocation loc) {
        return ResourceKey.create(Registries.WORLD_PRESET, loc);
    }

    public static ResourceKey<WorldPreset> getDefault() {
        return DEFAULT;
    }

    public static void suggestDefault(ResourceKey<WorldPreset> preset, int priority) {
        if (priority > currentDefaultPriority) {
            DEFAULT = preset;
            currentDefaultPriority = priority;
        }
    }

    private static void onBootstrap(BootstapContext<WorldPreset> context) {
        final WorldPresetBootstrapContext ctx
                = new WorldPresetBootstrapContext(context);

        BOOTSTRAP_WORLD_PRESETS.emit(c -> c.bootstrap(ctx));
    }
}
