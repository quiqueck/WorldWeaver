package de.ambertation.wover.preset.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.preset.api.WorldPresetInfo;
import de.ambertation.wover.preset.api.WorldPresetInfoRegistry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class WorldPresetInfoRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<WorldPresetInfo>> BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY
            = new EventImpl<>("BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY");

    private static void onBootstrap(BootstrapContext<WorldPresetInfo> ctx) {
        BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.register(
                WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY,
                WorldPresetInfoImpl.CODEC,
                WorldPresetInfoRegistryImpl::onBootstrap
        );

    }

    public static ResourceKey<WorldPresetInfo> createKey(
            Identifier ruleID
    ) {
        return ResourceKey.create(
                WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY,
                ruleID
        );
    }

    @ApiStatus.Internal
    public static Holder<WorldPresetInfo> register(
            @NotNull BootstrapContext<WorldPresetInfo> ctx,
            @NotNull ResourceKey<WorldPreset> key,
            @NotNull WorldPresetInfo info
    ) {
        if (info == null) {
            throw new IllegalStateException("World preset info is not set for world preset '" + key.identifier() + "'");
        }

        return ctx.register(
                createKey(key.identifier()),
                info
        );
    }
}
