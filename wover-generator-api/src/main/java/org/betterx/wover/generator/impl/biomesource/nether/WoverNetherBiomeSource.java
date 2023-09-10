package org.betterx.wover.generator.impl.biomesource.nether;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.biomesource.WoverBiomeSource;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceWithConfigScreen;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.client.NetherConfigPage;
import org.betterx.wover.generator.impl.map.MapStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WoverNetherBiomeSource extends WoverBiomeSource implements
        BiomeSourceWithConfig<WoverNetherBiomeSource, WoverNetherConfig>,
        BiomeSourceWithConfigScreen<WoverNetherBiomeSource, WoverNetherConfig> {
    public static final Codec<WoverNetherBiomeSource> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(
                            Codec
                                    .LONG
                                    .fieldOf("seed")
                                    .stable()
                                    .forGetter(source -> source.currentSeed),
                            WoverNetherConfig
                                    .CODEC
                                    .fieldOf("config").orElse(WoverNetherConfig.DEFAULT)
                                    .forGetter(o -> o.config)
                    )
                    .apply(instance, instance.stable(WoverNetherBiomeSource::new))
            );
    private static final List<TagKey<Biome>> TAGS = List.of(BiomeTags.IS_NETHER);
    private BiomeMap biomeMap;
    private WoverBiomePicker biomePicker;
    private WoverNetherConfig config;

    public WoverNetherBiomeSource(
            WoverNetherConfig config
    ) {
        this(0, config, false);
    }

    private WoverNetherBiomeSource(
            long seed,
            WoverNetherConfig config
    ) {
        this(seed, config, true);
    }


    private WoverNetherBiomeSource(
            long seed,
            WoverNetherConfig config,
            boolean initMaps
    ) {
        super(seed);
        this.config = config;
        rebuildBiomes(false);
        if (initMaps) {
            initMap(seed);
        }
    }

    @Override
    protected List<TagToPicker> createFreshPickerMap() {
        this.biomePicker = new WoverBiomePicker(fallbackBiome());
        return List.of(new TagToPicker(BiomeTags.IS_NETHER, biomePicker));
    }

    @Override
    protected TagKey<Biome> defaultBiomeTag() {
        return BiomeTags.IS_NETHER;
    }

    @Override
    protected List<TagKey<Biome>> acceptedTags() {
        return TAGS;
    }

    @Override
    protected TagKey<Biome> tagForUnknownBiome(Holder<Biome> biomeHolder, ResourceKey<Biome> biomeKey) {
        return defaultBiomeTag();
    }

    @Override
    protected ResourceKey<Biome> fallbackBiome() {
        return Biomes.NETHER_WASTES;
    }

    @Override
    public String toShortString() {
        return "WoVer - Nether BiomeSource (" + Integer.toHexString(hashCode()) + ")";
    }

    @Override
    public String toString() {
        return "\n" + toShortString() +
                "\n    biomes     = " + possibleBiomes().size() +
                "\n    namespaces = " + getNamespaces() +
                "\n    seed       = " + currentSeed +
                "\n    height     = " + maxHeight +
                "\n    config     = " + config;
    }

    @Override
    protected void onInitMap(long newSeed) {
        MapBuilderFunction mapConstructor = config.mapVersion.mapBuilder;
        if (maxHeight > config.biomeSizeVertical * 1.5 && config.useVerticalBiomes) {
            this.biomeMap = new MapStack(
                    newSeed,
                    config.biomeSize,
                    biomePicker,
                    config.biomeSizeVertical,
                    maxHeight,
                    mapConstructor
            );
        } else {
            this.biomeMap = mapConstructor.create(
                    newSeed,
                    config.biomeSize,
                    biomePicker
            );
        }
    }

    @Override
    protected void onHeightChange(int newHeight) {
        initMap(currentSeed);
    }

    @Override
    protected @NotNull Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler var4) {
        if (!wasBound()) reloadBiomes(false);

        if (biomeMap == null)
            return this.possibleBiomes().stream().findFirst().get();

        if ((biomeX & 63) == 0 && (biomeZ & 63) == 0) {
            biomeMap.clearCache();
        }
        WoverBiomePicker.PickableBiome bb = biomeMap.getBiome(biomeX << 2, biomeY << 2, biomeZ << 2);
        return bb.biome;
    }


    @Override
    public WoverNetherConfig getBiomeSourceConfig() {
        return config;
    }

    @Override
    public void setBiomeSourceConfig(WoverNetherConfig newConfig) {
        this.config = newConfig;
        initMap(currentSeed);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BiomeSourceConfigPanel<WoverNetherBiomeSource, WoverNetherConfig> biomeSourceConfigPanel(
            @NotNull Screen parent
    ) {
        return new NetherConfigPage(config);
    }
}
