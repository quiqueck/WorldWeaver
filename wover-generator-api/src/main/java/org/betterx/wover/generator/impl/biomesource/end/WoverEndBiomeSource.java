package org.betterx.wover.generator.impl.biomesource.end;

import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceWithConfigScreen;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.impl.client.EndConfigPage;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.screens.Screen;
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.awt.*;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WoverEndBiomeSource extends WoverBiomeSource implements
        BiomeSourceWithConfig<WoverEndBiomeSource, WoverEndConfig>,
        BiomeSourceWithConfigScreen<WoverEndBiomeSource, WoverEndConfig> {

    public static Codec<WoverEndBiomeSource> CODEC
            = RecordCodecBuilder.create((instance) -> instance
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
        this.endVoidBiomePicker = new WoverBiomePicker(fallbackBiome());
        this.endCenterBiomePicker = new WoverBiomePicker(fallbackBiome());
        this.endBarrensBiomePicker = new WoverBiomePicker(fallbackBiome());

        return List.of(
                new TagToPicker(CommonBiomeTags.IS_END_CENTER, endCenterBiomePicker),
                new TagToPicker(CommonBiomeTags.IS_END_BARRENS, endBarrensBiomePicker),
                new TagToPicker(CommonBiomeTags.IS_SMALL_END_ISLAND, endVoidBiomePicker),
                new TagToPicker(CommonBiomeTags.IS_END_HIGHLAND, endLandBiomePicker),
                new TagToPicker(CommonBiomeTags.IS_END_MIDLAND, endLandBiomePicker),
                new TagToPicker(BiomeTags.IS_END, endLandBiomePicker)
        );
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
        return "\n" + toShortString() +
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
            decider.createMap((picker, size) -> config.mapVersion.mapBuilder.create(
                    newSeed,
                    size <= 0 ? config.landBiomesSize : size,
                    picker
            ));
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

        if (endVoidBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen())
                WoverWorldGenerator.C.log.verbose("No Void Biomes found. Disabling by using barrens");
            endVoidBiomePicker = endBarrensBiomePicker;
        }
        if (endBarrensBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen())
                WoverWorldGenerator.C.log.verbose("No Barrens Biomes found. Disabling by using land Biomes");
            endBarrensBiomePicker = endLandBiomePicker;
            endVoidBiomePicker = endLandBiomePicker;
        }
        if (endCenterBiomePicker.isEmpty()) {
            if (!ModCore.isDatagen())
                WoverWorldGenerator.C.log.verbose("No Center Island Biomes found. Forcing use of vanilla center.");
            endCenterBiomePicker.addBiome(BiomeDataRegistryImpl.getFromRegistryOrTemp(Biomes.THE_END));
            endCenterBiomePicker.rebuild();
            if (endCenterBiomePicker.isEmpty()) {
                if (!ModCore.isDatagen())
                    WoverWorldGenerator.C.log.verbose(
                            "Unable to force vanilla central Island. Falling back to land Biomes...");
                endCenterBiomePicker = endLandBiomePicker;
            }
        }
    }

    @Override
    protected @NotNull Codec<? extends BiomeSource> codec() {
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
            mapVoid.clearCache();
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
                suggestedType = CommonBiomeTags.IS_END_HIGHLAND; //midlands
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

    @Override
    @Environment(EnvType.CLIENT)
    public BiomeSourceConfigPanel<WoverEndBiomeSource, WoverEndConfig> biomeSourceConfigPanel(@NotNull Screen parent) {
        return new EndConfigPage(config);
    }
}
