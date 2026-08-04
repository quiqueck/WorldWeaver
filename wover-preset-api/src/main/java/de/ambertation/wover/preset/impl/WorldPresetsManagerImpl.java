package de.ambertation.wover.preset.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;
import de.ambertation.wover.preset.api.event.OnBootstrapWorldPresets;
import de.ambertation.wover.preset.mixin.WorldPresetAccessor;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class WorldPresetsManagerImpl {
    public static final EventImpl<OnBootstrapWorldPresets> BOOTSTRAP_WORLD_PRESETS = new EventImpl<>(
            "BOOTSTRAP_WORLD_PRESETS");
    private static ResourceKey<WorldPreset> DEFAULT = net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL;
    private static int currentDefaultPriority = Integer.MIN_VALUE;

    public static Holder<WorldPreset> get(RegistryAccess access, ResourceKey<WorldPreset> key) {
        return access == null ? null : access
                .lookupOrThrow(Registries.WORLD_PRESET)
                .getOrThrow(key);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
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

    private static void onBootstrap(BootstrapContext<WorldPreset> context) {
        final WorldPresetBootstrapContext ctx
                = new WorldPresetBootstrapContext(context);

        BOOTSTRAP_WORLD_PRESETS.emit(c -> c.bootstrap(ctx));
    }

    public static WorldPreset withDimensions(
            WorldDimensions dimensions
    ) {
        Map<ResourceKey<LevelStem>, LevelStem> map = new HashMap<>();
        for (var entry : dimensions.dimensions().entrySet()) {
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem stem = entry.getValue();
            map.put(key, stem);
        }
        return new WorldPreset(map);
    }

    public static Map<ResourceKey<LevelStem>, LevelStem> getDimensions(Holder<WorldPreset> preset) {
        if (preset.value() instanceof WorldPresetAccessor acc)
            return acc.wover_getDimensions();

        return Map.of();
    }

    public static LevelStem getDimension(Holder<WorldPreset> preset, ResourceKey<LevelStem> key) {
        return getDimensions(preset).get(key);
    }

    public static WorldPreset fromStems(
            LevelStem overworldStem,
            LevelStem netherStem,
            LevelStem endStem
    ) {
        return new WorldPreset(Map.of(
                LevelStem.OVERWORLD, overworldStem,
                LevelStem.NETHER, netherStem,
                LevelStem.END, endStem
        ));
    }
}
