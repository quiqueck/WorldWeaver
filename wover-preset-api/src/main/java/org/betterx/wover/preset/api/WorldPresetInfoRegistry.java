package org.betterx.wover.preset.api;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverWorldPreset;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.preset.impl.WorldPresetInfoImpl;
import org.betterx.wover.preset.impl.WorldPresetInfoRegistryImpl;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class WorldPresetInfoRegistry {
    public static final Event<OnBootstrapRegistry<WorldPresetInfo>> BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY
            = WorldPresetInfoRegistryImpl.BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY;

    private WorldPresetInfoRegistry() {
    }

    public static final ResourceKey<Registry<WorldPresetInfo>> WORLD_PRESET_INFO_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverWorldPreset.C.id("wover/world_preset_info"));

    public static ResourceKey<WorldPresetInfo> createKey(
            ResourceLocation ruleID
    ) {
        return WorldPresetInfoRegistryImpl.createKey(ruleID);
    }

    public static ResourceKey<WorldPresetInfo> createKey(
            ResourceKey<WorldPreset> ruleID
    ) {
        return createKey(ruleID.location());
    }

    public static @NotNull WorldPresetInfo getFor(
            ResourceKey<WorldPreset> key
    ) {
        if (key == null) return WorldPresetInfoImpl.DEFAULT;
        final Registry<WorldPresetInfo> infos = WorldState.allStageRegistryAccess()
                                                          .registryOrThrow(WORLD_PRESET_INFO_REGISTRY);

        if (!infos.containsKey(key.location())) return WorldPresetInfoImpl.DEFAULT;
        return infos.get(key.location());
    }

    public static @NotNull WorldPresetInfo getFor(
            Holder<WorldPreset> holder
    ) {
        if (holder != null && holder.unwrapKey().isPresent()) return getFor(holder.unwrapKey().get());
        return WorldPresetInfoImpl.DEFAULT;
    }

    public static @NotNull WorldPresetInfo getFor(
            WorldPreset preset
    ) {
        if (preset == null) return WorldPresetInfoImpl.DEFAULT;

        final Registry<WorldPreset> presets = WorldState.allStageRegistryAccess()
                                                        .registryOrThrow(Registries.WORLD_PRESET);
        final Optional<ResourceKey<WorldPreset>> key = presets.getResourceKey(preset);
        if (key.isPresent()) return getFor(key.get());
        return WorldPresetInfoImpl.DEFAULT;
    }
}
