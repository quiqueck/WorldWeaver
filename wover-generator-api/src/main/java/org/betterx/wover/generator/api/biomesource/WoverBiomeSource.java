package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.modification.BiomeTagModificationWorker;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithSeed;
import org.betterx.wover.common.generator.api.biomesource.MergeableBiomeSource;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeSourceImpl;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for WoVer's tag-driven {@link BiomeSource} implementations
 * ({@link org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource
 * WoverNetherBiomeSource}/{@link org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource
 * WoverEndBiomeSource}).
 * <p>
 * A {@link WoverBiomeSource} places Biomes based on the Biome {@link net.minecraft.tags.TagKey}s returned by
 * {@link #acceptedTags()} (e.g. {@code minecraft:is_nether}): every registered Biome carrying one of those
 * tags is collected into a {@link WoverBiomePicker} for that tag (see {@link #createFreshPickerMap()}), and
 * subclasses use those pickers from their {@code getNoiseBiome(...)} implementation to decide which Biome
 * generates at a given position. Implements {@link ReloadableBiomeSource} to rebuild the pickers
 * whenever the Biome tags change (e.g. after a datapack reload) and {@link MergeableBiomeSource} to absorb
 * Biomes from another mod's {@link BiomeSource} that replaces this one (see
 * {@link org.betterx.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator
 * EnforceableChunkGenerator}).
 */
public abstract class WoverBiomeSource extends BiomeSource implements
        ReloadableBiomeSource,
        BiomeSourceWithNoiseRelatedSettings,
        BiomeSourceWithSeed,
        MergeableBiomeSource<WoverBiomeSource> {
    // volatile + synchronized rebuild/reload: getNoiseBiome lazily calls reloadBiomes(false) off the chunk
    // worker threads, so concurrent first-samples must not both run the (HashMap-mutating) picker rebuild.
    private volatile boolean didCreatePickers;
    Set<Holder<Biome>> dynamicPossibleBiomes;
    /**
     * The world seed this source currently generates with. Updated by {@link #setSeed(long)}.
     */
    protected long currentSeed;
    /**
     * The current world height. Updated by {@link #setMaxHeight(int)}.
     */
    protected int maxHeight;

    /**
     * Adds a Biome to a {@link WoverBiomePicker}, used by {@link #rebuildBiomes(boolean)} to feed every
     * accepted Biome into the matching picker(s).
     */
    @FunctionalInterface
    public interface PickerAdder {
        /**
         * Adds {@code bclBiome} to {@code picker} if it is a suitable Biome for {@code type}.
         *
         * @param bclBiome the Biome to add
         * @param type     the tag the Biome was matched against
         * @param picker   the picker to add the Biome to
         * @return {@code true} if the Biome was added and other pickers should not receive it as well
         */
        boolean add(BiomeData bclBiome, TagKey<Biome> type, WoverBiomePicker picker);
    }

    /**
     * Builds the list of {@link TagToPicker} pairs a {@link WoverBiomeSource} uses, one per accepted Biome
     * tag.
     */
    @FunctionalInterface
    public interface PickerMapFactory {
        /**
         * Creates the picker map.
         *
         * @param biomeDataRegistry the current {@link BiomeData} registry
         * @return the list of tag/picker pairs
         */
        List<TagToPicker> create(Registry<BiomeData> biomeDataRegistry);
    }

    /**
     * Associates a Biome tag with the {@link WoverBiomePicker} that picks Biomes carrying that tag.
     *
     * @param tag    the Biome tag
     * @param picker the picker for that tag
     */
    public record TagToPicker(TagKey<Biome> tag, WoverBiomePicker picker) {
    }

    /**
     * Creates a new instance.
     *
     * @param seed the initial world seed
     */
    public WoverBiomeSource(long seed) {
        didCreatePickers = false;
        dynamicPossibleBiomes = Set.of();
        currentSeed = seed;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        reloadBiomes();
        return dynamicPossibleBiomes.stream();
    }

    @Override
    final public void setSeed(long seed) {
        if (seed != currentSeed) {
            LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> new seed = " + seed);
            this.currentSeed = seed;
            initMap(seed);
        }
    }

    /**
     * Set world height
     *
     * @param maxHeight height of the World.
     */
    final public void setMaxHeight(int maxHeight) {
        if (this.maxHeight != maxHeight) {
            LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> new height = " + maxHeight);
            this.maxHeight = maxHeight;
            onHeightChange(maxHeight);
        }
    }

    /**
     * Checks whether the picker map was already built at least once (i.e. {@link #rebuildBiomes(boolean)}
     * ran).
     *
     * @return {@code true} once the pickers were built
     */
    protected boolean wasBound() {
        return didCreatePickers;
    }

    /**
     * The Biome tags this source places Biomes for, in priority order. Each tag gets its own
     * {@link WoverBiomePicker} in {@link #createFreshPickerMap()}.
     *
     * @return the accepted Biome tags
     */
    protected abstract List<TagKey<Biome>> acceptedTags();

    /**
     * The Biome to fall back to if a picker has no valid Biome to offer.
     *
     * @return the fallback Biome's key
     */
    protected abstract ResourceKey<Biome> fallbackBiome();

    /**
     * A short, human-readable description of this source instance, used in log messages.
     *
     * @return the description
     */
    public abstract String toShortString();

    /**
     * Called from {@link #initMap(long)} whenever the seed changed, so subclasses can (re)build their
     * {@link org.betterx.wover.generator.api.map.BiomeMap BiomeMap}(s).
     *
     * @param newSeed the new world seed
     */
    protected abstract void onInitMap(long newSeed);

    /**
     * Called from {@link #setMaxHeight(int)} whenever the world height changed.
     *
     * @param newHeight the new world height
     */
    protected abstract void onHeightChange(int newHeight);

    /**
     * The Biome tag used when no other tag was suggested for a Biome, i.e. the first entry of
     * {@link #acceptedTags()}.
     *
     * @return the default Biome tag
     */
    protected TagKey<Biome> defaultBiomeTag() {
        return acceptedTags().get(0);
    }

    /**
     * Builds a fresh {@link TagToPicker} list, with one new, empty {@link WoverBiomePicker} per entry of
     * {@link #acceptedTags()}. Subclasses that need to keep references to individual pickers (like
     * {@link org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource WoverEndBiomeSource})
     * override this to also store the pickers in dedicated fields.
     *
     * @return the fresh picker map
     */
    protected List<TagToPicker> createFreshPickerMap() {
        return acceptedTags().stream()
                             .map(tag -> new TagToPicker(tag, new WoverBiomePicker(fallbackBiome())))
                             .toList();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates {@link #maxHeight} from {@code generator}'s {@link NoiseGeneratorSettings#noiseSettings()}.
     */
    @Override
    public void onLoadGeneratorSettings(NoiseGeneratorSettings generator) {
        this.setMaxHeight(generator.noiseSettings().height());
    }

    /**
     * Called from {@link #rebuildBiomes(boolean)} once every picker in {@code pickerMap} was populated, to
     * rebuild each picker's weighted search tree. Subclasses that maintain extra pickers or
     * {@link org.betterx.wover.generator.api.biomesource.end.BiomeDecider BiomeDecider}s override this to
     * also rebuild those.
     *
     * @param pickerMap the freshly populated picker map
     */
    protected void onFinishBiomeRebuild(List<TagToPicker> pickerMap) {
        for (TagToPicker tagToPicker : pickerMap) {
            tagToPicker.picker.rebuild();
        }
    }

    /**
     * Lists the namespaces (and per-namespace Biome counts) of {@link #possibleBiomes()}, used in log
     * messages.
     *
     * @return the comma-separated namespace summary
     */
    @NotNull
    protected String getNamespaces() {
        return WoverBiomeSourceImpl.getNamespaces(possibleBiomes());
    }

    /**
     * Called from {@link #mergeWithBiomeSource(BiomeSource)} to decide which of {@link #acceptedTags()} a
     * foreign Biome (one that does not yet carry any of them) should be added to.
     * <p>
     * The default implementation returns the first accepted tag the Biome already carries, falling back to
     * {@link #defaultBiomeTag()}.
     *
     * @param biomeHolder the foreign Biome
     * @param biomeKey    the key of the foreign Biome
     * @return the tag the Biome should be added to
     */
    protected TagKey<Biome> tagForUnknownBiome(
            Holder<Biome> biomeHolder,
            ResourceKey<Biome> biomeKey
    ) {
        for (TagKey<Biome> type : acceptedTags()) {
            if (biomeHolder.is(type)) {
                return type;
            }
        }
        return defaultBiomeTag();
    }

    /**
     * The default {@link PickerAdder} implementation used by {@link #rebuildBiomes(boolean)}: adds every
     * matching Biome to its picker unconditionally.
     * <p>
     * Subclasses that need extra filtering (e.g. only including a Biome once for the highest-priority tag
     * it matches) override this.
     *
     * @param biomeData the Biome to add
     * @param type      the tag the Biome was matched against
     * @param picker    the picker to add the Biome to
     * @return always {@code true}
     */
    protected boolean addToPicker(BiomeData biomeData, TagKey<Biome> type, WoverBiomePicker picker) {
        picker.addBiome(biomeData);
        return true;
    }


    /**
     * Rebuilds the picker map from the currently registered Biomes.
     * <p>
     * Builds a fresh {@link #createFreshPickerMap() picker map}, populates it with every Biome carrying one
     * of {@link #acceptedTags()} (via {@link #addToPicker(BiomeData, TagKey, WoverBiomePicker)}), and
     * finishes with {@link #onFinishBiomeRebuild(List)}.
     *
     * @param force if {@code false}, does nothing once the pickers were already built at least once (see
     *              {@link #wasBound()})
     */
    protected final synchronized void rebuildBiomes(boolean force) {
        if (!force && didCreatePickers) return;

        LibWoverWorldGenerator.C.log.verbose("Updating Pickers for " + this.toShortString());

        final List<TagToPicker> pickers = createFreshPickerMap();
        this.dynamicPossibleBiomes = WoverBiomeSourceImpl.populateBiomePickers(
                pickers,
                this::addToPicker
        );

        if (this.dynamicPossibleBiomes == null) {
            this.dynamicPossibleBiomes = Set.of();
        }
        this.didCreatePickers = true;

        onFinishBiomeRebuild(pickers);
    }

    /**
     * Rebuilds the picker map (see {@link #rebuildBiomes(boolean)}) and then re-initializes the
     * {@link org.betterx.wover.generator.api.map.BiomeMap BiomeMap}(s) for the current seed.
     *
     * @param force if {@code false}, skips the picker rebuild if it already ran once
     */
    protected synchronized void reloadBiomes(boolean force) {
        rebuildBiomes(force);
        this.initMap(currentSeed);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Always forces a full rebuild of the picker map and {@link org.betterx.wover.generator.api.map.BiomeMap
     * BiomeMap}(s).
     */
    @Override
    public void reloadBiomes() {
        reloadBiomes(true);
    }

    /**
     * Re-initializes this source's {@link org.betterx.wover.generator.api.map.BiomeMap BiomeMap}(s) for the
     * given seed, delegating to {@link #onInitMap(long)}.
     *
     * @param seed the seed to initialize the map(s) with
     */
    protected final void initMap(long seed) {
        LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> Map Update");
        onInitMap(seed);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Every Biome offered by {@code inputBiomeSource} that does not already carry one of
     * {@link #acceptedTags()} is added to the matching tag (see
     * {@link #tagForUnknownBiome(Holder, ResourceKey)}), then {@link #reloadBiomes()} is called to rebuild
     * the picker map with the newly tagged Biomes included.
     */
    @Override
    public WoverBiomeSource mergeWithBiomeSource(BiomeSource inputBiomeSource) {
        Stopwatch sw = Stopwatch.createStarted();
        RegistryAccess access = WorldState.registryAccess();
        if (access == null) {
            access = WorldState.allStageRegistryAccess();
            if (access != null) {
                LibWoverWorldGenerator.C.log.verbose("Registries were not finalized before merging biome sources!");
            } else {
                LibWoverWorldGenerator.C.log.error("Unable to merge Biome Sources");
                return this;
            }
        }
        final Registry<Biome> biomes = access.lookupOrThrow(Registries.BIOME);

        final BiomeTagModificationWorker biomeTagWorker = new BiomeTagModificationWorker();
        int biomesAdded = 0;
        try {
            for (Holder<Biome> biomeHolder : inputBiomeSource.possibleBiomes()) {
                if (biomeHolder.unwrapKey().isPresent()) {
                    final ResourceKey<Biome> key = biomeHolder.unwrapKey().orElseThrow();
                    TagKey<Biome> tag = tagForUnknownBiome(biomeHolder, key);

                    if (tag != null && !biomeHolder.is(tag)) {
                        biomeTagWorker.addBiomeToTag(tag, biomes, key, biomeHolder);
                        biomesAdded++;
                    }
                }
            }

            biomeTagWorker.finished();
        } catch (RuntimeException e) {
            LibWoverWorldGenerator.C.log.error("Error while rebuilding BiomeSources!", e);
        } catch (Exception e) {
            LibWoverWorldGenerator.C.log.error("Error while rebuilding BiomeSources!", e);
        }

        this.reloadBiomes();
        if (biomesAdded > 0) {
            LibWoverWorldGenerator.C.log.info("Merged {} biomes to {} in {}", biomesAdded, toShortString(), sw);
        }
        return this;
    }
}
