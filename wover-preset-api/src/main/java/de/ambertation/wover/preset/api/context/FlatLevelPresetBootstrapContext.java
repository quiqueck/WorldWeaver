package de.ambertation.wover.preset.api.context;

import de.ambertation.wover.preset.impl.flat.FlatLevelPresetManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.BootstrapContextAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Special bootstrap context for flat level presets.
 * <p>
 * The context is created from a regular {@link BootstrapContext} and provides
 * a few helper methods specialized for flat level presets.
 * <p>
 * Minecraft 26.3 split {@link BootstrapContext} in two: the registering half stayed behind, while the
 * read-only half (looking registries up and listing what is in scope) moved to
 * {@link BootstrapContextAccess}. That is exactly what this class exposes on top of its own specialized
 * {@code register} method, so it now implements that interface instead of hand-rolling a single
 * {@code lookup}.
 */
public class FlatLevelPresetBootstrapContext implements BootstrapContextAccess {
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
     * Until Minecraft 26.3 there was a second overload that additionally took a
     * {@code com.mojang.serialization.Lifecycle}. It never had any effect - it always forwarded to the
     * two-argument {@code BootstrapContext#register(ResourceKey, Object)} and silently discarded the
     * lifecycle - and 26.3 removed the lifecycle-aware {@code BootstrapContext#register} entirely, so
     * there is no longer any way to honour such an argument. The overload was therefore dropped rather
     * than kept as a parameter that lies about what it does; every preset is registered with the
     * registry's own lifecycle.
     * <p>
     * <b>Layer order:</b> {@code flatLayerInfos} is consumed <b>bottom layer first</b>, matching vanilla -
     * which reads {@code FlatLevelGeneratorSettings#getLayersInfo()} bottom-up, index 0 being the lowest
     * y - and the documentation in {@code public/wiki/modules/preset-api.md}. Up to and including the
     * 26.3 port the array was iterated back-to-front, so the <em>last</em> argument became the bottom-most
     * layer and every flat preset generated inverted relative to what its author wrote. Presets that were
     * authored against the old behaviour need their layer arguments reversed.
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
     * <p>
     * This used to be declared as {@code lookup(ResourceKey<? extends Registry<? extends
     * FlatLevelGeneratorPreset>>)}, which - despite what the description says - could only ever look up
     * the flat level preset registry itself. Now that the method comes from
     * {@link BootstrapContextAccess} it carries vanilla's own type parameter and can genuinely reach
     * "other available registries", just like the identically named method on
     * {@link WorldPresetBootstrapContext} always could.
     *
     * @param resourceKey the key of the registry
     * @param <S>         the element type of the registry
     * @return a readonly view for the registry
     */
    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
        return context.lookup(resourceKey);
    }

    /**
     * Lists every element that is visible in the surrounding bootstrap context for the given registry.
     * <p>
     * New in Minecraft 26.3 (see {@link BootstrapContextAccess}). As this class is a decorator around
     * another context, we forward it unchanged - the wrapped context is the one that knows what is
     * actually in scope.
     *
     * @param resourceKey the key of the registry
     * @param <S>         the element type of the registry
     * @return the elements of that registry that are visible in this context
     */
    @Override
    public <S> Stream<Holder.Reference<S>> listContextElements(
            ResourceKey<? extends Registry<? extends S>> resourceKey
    ) {
        return context.listContextElements(resourceKey);
    }
}
