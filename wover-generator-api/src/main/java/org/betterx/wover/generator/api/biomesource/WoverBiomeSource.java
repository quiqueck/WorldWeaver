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

public abstract class WoverBiomeSource extends BiomeSource implements
        ReloadableBiomeSource,
        BiomeSourceWithNoiseRelatedSettings,
        BiomeSourceWithSeed,
        MergeableBiomeSource<WoverBiomeSource> {
    private boolean didCreatePickers;
    Set<Holder<Biome>> dynamicPossibleBiomes;
    protected long currentSeed;
    protected int maxHeight;

    @FunctionalInterface
    public interface PickerAdder {
        boolean add(BiomeData bclBiome, TagKey<Biome> type, WoverBiomePicker picker);
    }

    @FunctionalInterface
    public interface PickerMapFactory {
        List<TagToPicker> create(Registry<BiomeData> biomeDataRegistry);
    }

    public record TagToPicker(TagKey<Biome> tag, WoverBiomePicker picker) {
    }

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

    protected boolean wasBound() {
        return didCreatePickers;
    }

    protected abstract List<TagKey<Biome>> acceptedTags();

    protected abstract ResourceKey<Biome> fallbackBiome();

    public abstract String toShortString();

    protected abstract void onInitMap(long newSeed);
    protected abstract void onHeightChange(int newHeight);

    protected TagKey<Biome> defaultBiomeTag() {
        return acceptedTags().get(0);
    }

    protected List<TagToPicker> createFreshPickerMap() {
        return acceptedTags().stream()
                             .map(tag -> new TagToPicker(tag, new WoverBiomePicker(fallbackBiome())))
                             .toList();
    }

    @Override
    public void onLoadGeneratorSettings(NoiseGeneratorSettings generator) {
        this.setMaxHeight(generator.noiseSettings().height());
    }

    protected void onFinishBiomeRebuild(List<TagToPicker> pickerMap) {
        for (TagToPicker tagToPicker : pickerMap) {
            tagToPicker.picker.rebuild();
        }
    }

    @NotNull
    protected String getNamespaces() {
        return WoverBiomeSourceImpl.getNamespaces(possibleBiomes());
    }

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

    protected boolean addToPicker(BiomeData biomeData, TagKey<Biome> type, WoverBiomePicker picker) {
        picker.addBiome(biomeData);
        return true;
    }


    protected final void rebuildBiomes(boolean force) {
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

    protected void reloadBiomes(boolean force) {
        rebuildBiomes(force);
        this.initMap(currentSeed);
    }

    @Override
    public void reloadBiomes() {
        reloadBiomes(true);
    }

    protected final void initMap(long seed) {
        LibWoverWorldGenerator.C.log.debug(this.toShortString() + "\n    --> Map Update");
        onInitMap(seed);
    }

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
        final Registry<Biome> biomes = access.registryOrThrow(Registries.BIOME);

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
