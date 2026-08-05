package de.ambertation.wover.preset.impl.flat;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.preset.api.context.FlatLevelPresetBootstrapContext;
import de.ambertation.wover.preset.api.event.OnBootstrapFlatLevelPresets;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;

public class FlatLevelPresetManagerImpl {
    public static final EventImpl<OnBootstrapFlatLevelPresets> BOOTSTRAP_FLAT_LEVEL_PRESETS = new EventImpl<>(
            "BOOTSTRAP_FLAT_LEVEL_PRESETS");

    public static ResourceKey<FlatLevelGeneratorPreset> createKey(Identifier loc) {
        return ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, loc);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.FLAT_LEVEL_GENERATOR_PRESET,
                FlatLevelPresetManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstrapContext<FlatLevelGeneratorPreset> context) {
        FlatLevelPresetBootstrapContext ctx = new FlatLevelPresetBootstrapContext(context);
        BOOTSTRAP_FLAT_LEVEL_PRESETS.emit(c -> c.bootstrap(ctx));
    }

    /**
     * The single implementation of flat-preset registration.
     * <p>
     * {@link de.ambertation.wover.preset.api.context.FlatLevelPresetBootstrapContext#register} used to
     * carry a byte-for-byte copy of this body, which is how the layer-order bug below managed to exist
     * in two places at once. It now delegates here.
     * <p>
     * {@code flatLayerInfos} is consumed <b>bottom layer first</b>, matching both vanilla - which reads
     * {@link FlatLevelGeneratorSettings#getLayersInfo()} bottom-up, index 0 being the lowest y - and the
     * documentation in {@code public/wiki/modules/preset-api.md}. Before this was fixed the array was
     * iterated back-to-front, so the <em>last</em> argument became the bottom-most layer and every flat
     * preset generated inverted relative to what its author wrote.
     *
     * @return the holder of the registered preset
     */
    public static Holder.Reference<FlatLevelGeneratorPreset> register(
            BootstrapContext<FlatLevelGeneratorPreset> ctx,
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

        flatLevelGeneratorSettings.getLayersInfo().addAll(List.of(flatLayerInfos));

        return ctx.register(
                presetKey,
                new FlatLevelGeneratorPreset(icon.asItem().builtInRegistryHolder(), flatLevelGeneratorSettings)
        );
    }
}
