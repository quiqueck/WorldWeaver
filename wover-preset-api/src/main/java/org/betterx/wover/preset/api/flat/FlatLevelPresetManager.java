package org.betterx.wover.preset.api.flat;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.preset.api.event.OnBootstrapFlatLevelPresets;
import org.betterx.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for {@link FlatLevelGeneratorPreset}s.
 * <p>
 * The class provide an event for registering presets at Runtime. However, you should
 * consider generating presets in the data generator whenever possible (see WoverFlatLevelPresetProvider).
 */
public class FlatLevelPresetManager {
    private FlatLevelPresetManager() {
    }

    /**
     * The event that is fired when the Registry for {@link FlatLevelGeneratorPreset}s
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverFlatLevelPresetProvider) for Details.
     */
    public static final Event<OnBootstrapFlatLevelPresets> BOOTSTRAP_FLAT_LEVEL_PRESETS = FlatLevelPresetManagerImpl.BOOTSTRAP_FLAT_LEVEL_PRESETS;

    /**
     * Creates a {@link ResourceKey} for a {@link FlatLevelGeneratorPreset}.
     *
     * @param loc The location of the preset.
     * @return The key.
     */
    public static ResourceKey<FlatLevelGeneratorPreset> createKey(ResourceLocation loc) {
        return FlatLevelPresetManagerImpl.createKey(loc);
    }

    /**
     * Registers a {@link FlatLevelGeneratorPreset}.
     *
     * @param ctx                  The {@link BootstapContext} to register the preset in.
     * @param presetKey            The key of the preset.
     * @param icon                 The icon of the preset.
     * @param biomeKey             The key of the biome to use.
     * @param allowedStructureSets The allowed structure sets.
     * @param addDecorations       Whether to add decorations.
     * @param addLakes             Whether to add lakes.
     * @param flatLayerInfos       The flat layer infos.
     */
    public static void register(
            @NotNull BootstapContext<FlatLevelGeneratorPreset> ctx,
            @NotNull ResourceKey<FlatLevelGeneratorPreset> presetKey,
            @NotNull ItemLike icon,
            @NotNull ResourceKey<Biome> biomeKey,
            @Nullable Set<ResourceKey<StructureSet>> allowedStructureSets,
            boolean addDecorations,
            boolean addLakes,
            FlatLayerInfo... flatLayerInfos
    ) {
        FlatLevelPresetManagerImpl.register(
                ctx,
                presetKey,
                icon,
                biomeKey,
                allowedStructureSets == null ? Set.of() : allowedStructureSets,
                addDecorations,
                addLakes,
                flatLayerInfos
        );
    }
}
