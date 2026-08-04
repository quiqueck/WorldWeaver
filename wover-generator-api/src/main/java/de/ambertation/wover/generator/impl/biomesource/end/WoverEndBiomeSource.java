package de.ambertation.wover.generator.impl.biomesource.end;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.generator.api.biomesource.WoverBiomePicker;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeSource;
import de.ambertation.wover.generator.api.biomesource.end.BiomeDecider;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.api.map.BiomeMap;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WoverEndBiomeSource extends WoverBiomeSource implements
        BiomeSourceWithConfig<WoverEndBiomeSource, WoverEndConfig> {

    public static MapCodec<WoverEndBiomeSource> CODEC
            = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    Codec
                            .LONG
                            .fieldOf("seed")
                            .stable()
                            .forGetter(source -> source.currentSeed),
                    WoverEndConfig
                            .CODEC
                            .fieldOf("config")
                            .orElse(WoverEndConfig.DEFAULT)
                            .forGetter(o -> o.config)
            )
            .apply(
                    instance,
                    instance.stable(WoverEndBiomeSource::new)
            )
    );

    public static final List<TagKey<Biome>> TAGS = List.of(
            CommonBiomeTags.IS_END_CENTER,
            CommonBiomeTags.IS_END_BARRENS,
            CommonBiomeTags.IS_SMALL_END_ISLAND,
            CommonBiomeTags.IS_END_HIGHLAND,
            CommonBiomeTags.IS_END_MIDLAND,
            BiomeTags.IS_END
    );
    private final Point pos;
    private BiomeMap mapLand;
    private BiomeMap mapVoid;
    private BiomeMap mapCenter;
    private BiomeMap mapBarrens;

    private WoverBiomePicker endLandBiomePicker;
    private WoverBiomePicker endVoidBiomePicker;
    private WoverBiomePicker endCenterBiomePicker;
    private WoverBiomePicker endBarrensBiomePicker;
    private List<BiomeDecider> deciders;

    private WoverEndConfig config;

    private WoverEndBiomeSource(
            long seed,
            WoverEndConfig config
    ) {
        this(seed, config, true);
    }

    public WoverEndBiomeSource(
            WoverEndConfig config
    ) {
        this(0, config, false);
    }


    private WoverEndBiomeSource(
            long seed,
            WoverEndConfig config,
            boolean initMaps
    ) {
        super(seed);
        this.config = config;
        rebuildBiomes(false);

        this.pos = new Point();

        if (initMaps) {
            initMap(seed);
        }
    }

    @Override
    protected List<TagToPicker> createFreshPickerMap() {
        this.deciders = BiomeDeciderImpl.DECIDERS.stream()
                                                 .filter(d -> d.canProvideFor(this))
                                                 .map(d -> d.createInstance(this))
                                                 .toList();

        this.endLandBiomePicker = new WoverBiomePicker(fallbackBiome());
        this.endVoidBiomePicker = new WoverBiomePicker(Biomes.SMALL_END_ISLANDS);
        this.endCenterBiomePicker = new WoverBiomePicker(Biomes.THE_END);
        this.endBarrensBiomePicker = new WoverBiomePicker(Biomes.END_BARRENS);

        final List<TagToPicker> pickerMap = new ArrayList<>();

        // Decider-managed pickers come first: populateBiomePickers deduplicates biomes across the picker
        // map (a biome added to one picker is skipped for all later ones), so putting a decider's picker
        // ahead of the standard land/void/barrens entries keeps biomes carrying the decider's pickerTag out
        // of those default rings while still entering dynamicPossibleBiomes. The entries must reference the
        // same decider instances this source uses (see the `deciders` field created above), so that the
        // biomes populated here end up in the very pickers the deciders read from at generation time.
        for (BiomeDecider decider : deciders) {
            final TagKey<Biome> deciderTag = decider.pickerTag();
            final WoverBiomePicker deciderPicker = decider.picker();
            if (deciderTag != null && deciderPicker != null) {
                pickerMap.add(new TagToPicker(deciderTag, deciderPicker));
            }
        }

        pickerMap.add(new TagToPicker(CommonBiomeTags.IS_END_CENTER, endCenterBiomePicker));
        pickerMap.add(new TagToPicker(CommonBiomeTags.IS_END_BARRENS, endBarrensBiomePicker));
        pickerMap.add(new TagToPicker(CommonBiomeTags.IS_SMALL_END_ISLAND, endVoidBiomePicker));
        pickerMap.add(new TagToPicker(CommonBiomeTags.IS_END_HIGHLAND, endLandBiomePicker));
        pickerMap.add(new TagToPicker(CommonBiomeTags.IS_END_MIDLAND, endLandBiomePicker));
        pickerMap.add(new TagToPicker(BiomeTags.IS_END, endLandBiomePicker));

        return pickerMap;
    }

    @Override
    protected boolean addToPicker(BiomeData biomeData, TagKey<Biome> type, WoverBiomePicker picker) {
        picker.addBiome(biomeData);

        // A Biome is normally added to a single picker: the first accepted tag it carries, in
        // the order of createFreshPickerMap(). Because the barrens picker is visited *before* the
        // small-island (void) picker, a Biome whose intended placement is a small end island but
        // which is *also* tagged as barrens (e.g. betterend:ice_starfield, so it appears in both
        // the common barrens ring and its natural void ring) would otherwise land only in the
        // barrens picker, leaving the void picker empty. Returning false here means it is not yet
        // marked as fully placed, so the small-island picker (visited next) receives it as well;
        // it is finally deduplicated once added to its intended small-island picker.
        if (type.equals(CommonBiomeTags.IS_END_BARRENS)
                && biomeData.isIntendedFor(CommonBiomeTags.IS_SMALL_END_ISLAND)) {
            return false;
        }
        return true;
    }

    @Override
    protected TagKey<Biome> defaultBiomeTag() {
        return CommonBiomeTags.IS_END_HIGHLAND;
    }

    @Override
    protected List<TagKey<Biome>> acceptedTags() {
        return TAGS;
    }

    @Override
    protected ResourceKey<Biome> fallbackBiome() {
        return Biomes.END_HIGHLANDS;
    }

    @Override
    public String toShortString() {
        return "WoVer - The End  BiomeSource (" + Integer.toHexString(hashCode()) + ")";
    }

    @Override
    public String toString() {
        return toShortString() +
                "\n    biomes     = " + possibleBiomes().size() +
                "\n    namespaces = " + getNamespaces() +
                "\n    seed       = " + currentSeed +
                "\n    height     = " + maxHeight +
                "\n    deciders   = " + deciders.size() +
                "\n    config     = " + config;
    }

    @Override
    protected void onInitMap(long newSeed) {
        for (BiomeDecider decider : deciders) {
            decider.createMap(
                    (picker, size) -> config.mapVersion.mapBuilder.create(
                            newSeed,
                            size <= 0 ? config.landBiomesSize : size,
                            picker
                    ),
                    newSeed
            );
        }
        this.mapLand = config.mapVersion.mapBuilder.create(
                newSeed,
                config.landBiomesSize,
                endLandBiomePicker
        );

        this.mapVoid = config.mapVersion.mapBuilder.create(
                newSeed,
                config.voidBiomesSize,
                endVoidBiomePicker
        );

        this.mapCenter = config.mapVersion.mapBuilder.create(
                newSeed,
                config.centerBiomesSize,
                endCenterBiomePicker
        );

        this.mapBarrens = config.mapVersion.mapBuilder.create(
                newSeed,
                config.barrensBiomesSize,
                endBarrensBiomePicker
        );
    }

    @Override
    protected void onHeightChange(int newHeight) {

    }

    @Override
    protected void onFinishBiomeRebuild(List<TagToPicker> pickerMap) {
        super.onFinishBiomeRebuild(pickerMap);

        for (BiomeDecider decider : deciders) {
            decider.rebuild();
        }

        if (WorldState.allStageRegistryAccess() != null) {
            // Vanilla end_barrens is data-modelled as a highlands SUB-biome (see the vanilla biome
            // data), so tag-based picker population never adds it as a top-level member. It must
            // ALWAYS be part of the barrens ring, competing at its own genChance with any modded
            // barrens biomes - same idea as the vanilla-center fallback below, but unconditional.
            endBarrensBiomePicker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(Biomes.END_BARRENS));
            endBarrensBiomePicker.rebuild();
        }

        final boolean voidWasEmpty = endVoidBiomePicker.isEmpty();
        if (voidWasEmpty) {
            if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null)
                LibWoverWorldGenerator.C.log.verbose("No Void Biomes found. Disabling by using barrens");
            endVoidBiomePicker = endBarrensBiomePicker;
        }
        if (endBarrensBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null)
                LibWoverWorldGenerator.C.log.verbose("No Barrens Biomes found. Disabling by using land Biomes");
            endBarrensBiomePicker = endLandBiomePicker;
            // Only drag the void picker along when it was itself empty (the original both-empty
            // cascade). A healthy void picker must keep its own biomes: overwriting it here made the
            // whole void ring render land biomes as soon as ONLY the barrens tag was empty, silently
            // deleting every void-only biome (e.g. an ice-star field) from the world.
            if (voidWasEmpty) {
                endVoidBiomePicker = endLandBiomePicker;
            }
        }
        if (endCenterBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null)
                LibWoverWorldGenerator.C.log.verbose("No Center Island Biomes found. Forcing use of vanilla center.");
            endCenterBiomePicker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(Biomes.THE_END));
            endCenterBiomePicker.rebuild();
            if (endCenterBiomePicker.isEmpty()) {
                if (!ModCore.isDatagen() && WorldState.allStageRegistryAccess() != null)
                    LibWoverWorldGenerator.C.log.verbose(
                            "Unable to force vanilla central Island. Falling back to land Biomes...");
                endCenterBiomePicker = endLandBiomePicker;
            }
        }
    }

    @Override
    protected @NotNull MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int biomeX, int biomeY, int biomeZ, Climate.@NotNull Sampler sampler) {
        if (!wasBound()) reloadBiomes(false);

        if (mapLand == null || mapVoid == null || mapCenter == null || mapBarrens == null)
            return this.possibleBiomes().stream().findFirst().orElseThrow();

        int posX = QuartPos.toBlock(biomeX);
        int posY = QuartPos.toBlock(biomeY);
        int posZ = QuartPos.toBlock(biomeZ);

        long dist = Math.abs(posX) + Math.abs(posZ) > (long) config.innerVoidRadiusSquared
                ? ((long) config.innerVoidRadiusSquared + 1)
                : (long) posX * (long) posX + (long) posZ * (long) posZ;


        if ((biomeX & 63) == 0 || (biomeZ & 63) == 0) {
            mapLand.clearCache();
            mapVoid.clearCache();
            mapCenter.clearCache();
            mapBarrens.clearCache();
            for (BiomeDecider decider : deciders) {
                decider.clearMapCache();
            }
        }

        TagKey<Biome> suggestedType;


        int x = (SectionPos.blockToSectionCoord(posX) * 2 + 1) * 8;
        int z = (SectionPos.blockToSectionCoord(posZ) * 2 + 1) * 8;
        double d = sampler.erosion().compute(new DensityFunction.SinglePointContext(x, posY, z));
        if (dist <= (long) config.innerVoidRadiusSquared) {
            suggestedType = CommonBiomeTags.IS_END_CENTER;
        } else {
            if (d > 0.25) {
                suggestedType = CommonBiomeTags.IS_END_HIGHLAND; //highlands
            } else if (d >= -0.0625) {
                suggestedType = CommonBiomeTags.IS_END_MIDLAND; //midlands
            } else {
                suggestedType = d < -0.21875
                        ? CommonBiomeTags.IS_SMALL_END_ISLAND //small islands
                        : (config.withVoidBiomes
                                ? CommonBiomeTags.IS_END_BARRENS
                                : CommonBiomeTags.IS_END_HIGHLAND); //barrens
            }
        }

        final TagKey<Biome> originalType = suggestedType;
        for (BiomeDecider decider : deciders) {
            suggestedType = decider
                    .suggestType(originalType, suggestedType, d, maxHeight, posX, posY, posZ, biomeX, biomeY, biomeZ);
        }


        WoverBiomePicker.PickableBiome result;
        for (BiomeDecider decider : deciders) {
            if (decider.canProvideBiome(suggestedType)) {
                result = decider.provideBiome(suggestedType, posX, posY, posZ);
                if (result != null) return result.biome;
            }
        }

        if (suggestedType == CommonBiomeTags.IS_END_CENTER) return mapCenter.getBiome(posX, posY, posZ).biome;
        if (suggestedType == CommonBiomeTags.IS_SMALL_END_ISLAND) return mapVoid.getBiome(posX, posY, posZ).biome;
        if (suggestedType == CommonBiomeTags.IS_END_BARRENS) return mapBarrens.getBiome(posX, posY, posZ).biome;
        return mapLand.getBiome(posX, posY, posZ).biome;
    }

    /**
     * The prospective land (highlands/midlands) biome for the given world column.
     * <p>
     * Ring classification in {@link #getNoiseBiome} is Y-independent, so this returns the biome the land
     * map would place for the column regardless of height — i.e. the column's prospective surface biome.
     * A cave decider can use this to align its own placement with the surface biome above it.
     *
     * @param posX the block x coordinate
     * @param posZ the block z coordinate
     * @return the land biome for the column, or {@code null} if the land map has not been initialized yet
     * (i.e. before {@link #onInitMap(long)} ran)
     */
    public WoverBiomePicker.PickableBiome landBiomeAt(int posX, int posZ) {
        if (mapLand == null) return null;
        return mapLand.getBiome(posX, 0, posZ);
    }

    @Override
    public WoverEndConfig getBiomeSourceConfig() {
        return config;
    }

    @Override
    public void setBiomeSourceConfig(WoverEndConfig newConfig) {
        this.config = newConfig;
        rebuildBiomes(true);
        this.initMap(currentSeed);
    }

}
