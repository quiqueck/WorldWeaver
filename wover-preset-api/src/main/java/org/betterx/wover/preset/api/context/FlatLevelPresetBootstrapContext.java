package org.betterx.wover.preset.api.context;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Special bootstrap context for flat level presets.
 * <p>
 * The context is created from a regular {@link BootstapContext} and provides
 * a few helper methods specialized for flat level presets.
 */
public class FlatLevelPresetBootstrapContext {
    private final BootstapContext<FlatLevelGeneratorPreset> context;

    /**
     * Creates a new flat level preset bootstrap context from the given context.
     *
     * @param context the original context to wrap
     */
    public FlatLevelPresetBootstrapContext(BootstapContext<FlatLevelGeneratorPreset> context) {
        this.context = context;
    }


    /**
     * Registers a new flat level preset with the given parameters.
     * <p>
     * This is a convenience method that calls
     * {@link #register(ResourceKey, Lifecycle, ItemLike, ResourceKey, Set, boolean, boolean, FlatLayerInfo...)}
     * and uses {@link Lifecycle#stable()} as lifecycle.
     *
     * @param presetKey            the key of the preset
     * @param icon                 the icon you want to display for the preset in the UI
     * @param biomeKey             the key for the biome that will be used by the flat world
     * @param allowedStructureSets StructureSets that are allowed to generate in the world
     * @param addDecorations       should decorations be generated
     * @param addLakes             should lakes be generated
     * @param flatLayerInfos       the layers of the flat world
     * @return a holder to the created preset
     */
    public Holder.Reference<FlatLevelGeneratorPreset> register(
            ResourceKey<FlatLevelGeneratorPreset> presetKey,
            ItemLike icon,
            ResourceKey<Biome> biomeKey,
            Set<ResourceKey<StructureSet>> allowedStructureSets,
            boolean addDecorations,
            boolean addLakes,
            FlatLayerInfo... flatLayerInfos
    ) {
        return register(
                presetKey,
                Lifecycle.stable(),
                icon,
                biomeKey,
                allowedStructureSets,
                addDecorations,
                addLakes,
                flatLayerInfos
        );
    }

    /**
     * Registers a new flat level preset with the given parameters.
     *
     * @param presetKey            the key of the preset
     * @param lifecycle            the lifecycle of the preset
     * @param icon                 the icon you want to display for the preset in the UI
     * @param biomeKey             the key for the biome that will be used by the flat world
     * @param allowedStructureSets StructureSets that are allowed to generate in the world
     * @param addDecorations       should decorations be generated
     * @param addLakes             should lakes be generated
     * @param flatLayerInfos       the layers of the flat world
     * @return a holder to the created preset
     */
    public Holder.Reference<FlatLevelGeneratorPreset> register(
            ResourceKey<FlatLevelGeneratorPreset> presetKey,
            Lifecycle lifecycle,
            ItemLike icon,
            ResourceKey<Biome> biomeKey,
            Set<ResourceKey<StructureSet>> allowedStructureSets,
            boolean addDecorations,
            boolean addLakes,
            FlatLayerInfo... flatLayerInfos
    ) {
        final HolderGetter<StructureSet> structureSets = context.lookup(Registries.STRUCTURE_SET);
        Objects.requireNonNull(structureSets);

        final HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        final HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        HolderSet.Direct<StructureSet> structures = HolderSet.direct(
                allowedStructureSets.stream()
                                    .map(structureSets::getOrThrow)
                                    .toList());

        FlatLevelGeneratorSettings flatLevelGeneratorSettings = new FlatLevelGeneratorSettings(
                Optional.of(structures),
                biomes.getOrThrow(biomeKey),
                FlatLevelGeneratorSettings.createLakesList(placedFeatures)
        );
        if (addDecorations) {
            flatLevelGeneratorSettings.setDecoration();
        }

        if (addLakes) {
            flatLevelGeneratorSettings.setAddLakes();
        }

        for (int i = flatLayerInfos.length - 1; i >= 0; --i) {
            flatLevelGeneratorSettings.getLayersInfo().add(flatLayerInfos[i]);
        }

        return context.register(
                presetKey,
                new FlatLevelGeneratorPreset(icon.asItem().builtInRegistryHolder(), flatLevelGeneratorSettings)
        );
    }

    /**
     * Lookup other available registries.
     *
     * @param resourceKey the key of the registry
     * @return a readonly view for the registry
     */
    public HolderGetter<FlatLevelGeneratorPreset> lookup(ResourceKey<? extends Registry<? extends FlatLevelGeneratorPreset>> resourceKey) {
        return context.lookup(resourceKey);
    }
}
