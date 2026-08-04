package de.ambertation.wover.preset.api.context;

import de.ambertation.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Set;

/**
 * Special bootstrap context for flat level presets.
 * <p>
 * The context is created from a regular {@link BootstrapContext} and provides
 * a few helper methods specialized for flat level presets.
 */
public class FlatLevelPresetBootstrapContext {
    private final BootstrapContext<FlatLevelGeneratorPreset> context;

    /**
     * Creates a new flat level preset bootstrap context from the given context.
     *
     * @param context the original context to wrap
     */
    public FlatLevelPresetBootstrapContext(BootstrapContext<FlatLevelGeneratorPreset> context) {
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
     * <p>
     * <b>Layer order:</b> {@code flatLayerInfos} is consumed <b>bottom layer first</b>, matching vanilla -
     * which reads {@link FlatLevelGeneratorSettings#getLayersInfo()} bottom-up, index 0 being the lowest
     * y - and the documentation in {@code public/wiki/modules/preset-api.md}. Before this was fixed the
     * array was iterated back-to-front, so the <em>last</em> argument became the bottom-most layer and
     * every flat preset generated inverted relative to what its author wrote. Presets that were authored
     * against the old behaviour need their layer arguments reversed.
     *
     * @param presetKey            the key of the preset
     * @param lifecycle            the lifecycle of the preset. Unused - every preset is registered with
     *                             the registry's own lifecycle; kept so the published signature does not
     *                             change.
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
        // Delegates rather than duplicating: this method used to hold a byte-for-byte copy of
        // FlatLevelPresetManagerImpl#register, which is how the layer-order bug fixed above managed to
        // live in two places at once.
        return FlatLevelPresetManagerImpl.register(
                context,
                presetKey,
                icon,
                biomeKey,
                allowedStructureSets,
                addDecorations,
                addLakes,
                flatLayerInfos
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
