package org.betterx.wover.preset.impl.flat;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.preset.api.context.FlatLevelPresetBootstrapContext;
import org.betterx.wover.preset.api.event.OnBootstrapFlatLevelPresets;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    public static void register(
            BootstapContext<FlatLevelGeneratorPreset> ctx,
            ResourceKey<FlatLevelGeneratorPreset> presetKey,
            ItemLike icon,
            ResourceKey<Biome> biomeKey,
            Set<ResourceKey<StructureSet>> allowedStructureSets,
            boolean addDecorations,
            boolean addLakes,
            FlatLayerInfo... flatLayerInfos
    ) {
        final HolderGetter<StructureSet> structureSets = ctx.lookup(Registries.STRUCTURE_SET);
        Objects.requireNonNull(structureSets);

        final HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
        final HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);

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

        ctx.register(
                presetKey,
                new FlatLevelGeneratorPreset(icon.asItem().builtInRegistryHolder(), flatLevelGeneratorSettings)
        );
    }
}
