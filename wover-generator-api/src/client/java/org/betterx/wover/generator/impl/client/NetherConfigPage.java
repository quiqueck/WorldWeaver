package org.betterx.wover.generator.impl.client;

import de.ambertation.wunderlib.ui.layout.components.Checkbox;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.Range;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Value;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.chunkgenerator.WoverChunkGenerator;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.api.preset.PresetsRegistry;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.chunkgenerator.ConfiguredChunkGenerator;
import org.betterx.wover.generator.impl.chunkgenerator.DimensionsWrapper;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class NetherConfigPage implements BiomeSourceConfigPanel<WoverNetherBiomeSource, WoverNetherConfig> {
    private final Checkbox netherLegacy;
    private final Checkbox netherVertical;
    private final Checkbox netherAmplified;

    private final Range<Integer> netherBiomeSize;
    private final Range<Integer> netherVerticalBiomeSize;

    private final VerticalStack pageComponent;


    public NetherConfigPage(@NotNull WoverNetherConfig netherConfig) {
        VerticalStack content = new VerticalStack(Value.fill(), Value.fit()).centerHorizontal();
        content.addSpacer(8);

        netherLegacy = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.legacy_square"),
                netherConfig.mapVersion == WoverNetherConfig.NetherBiomeMapType.SQUARE
        );

        netherAmplified = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.nether_amplified"),
                netherConfig.amplified
        );

        netherVertical = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.nether_vertical"),
                netherConfig.useVerticalBiomes
        );

        content.addSpacer(12);
        content.addText(Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.avg_biome_size"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        netherBiomeSize = content.addRange(
                Value.fixed(200),
                Value.fit(),
                Component.translatable("title.screen.wover.worldgen.nether_biome_size"),
                1,
                512,
                netherConfig.biomeSize / 16
        );

        netherVerticalBiomeSize = content.addRange(
                Value.fixed(200),
                Value.fit(),
                Component.translatable("title.screen.wover.worldgen.nether_vertical_biome_size"),
                1,
                32,
                netherConfig.biomeSizeVertical / 16
        );

        content.addSpacer(8);
        content.setDebugName("Nether page");

        this.pageComponent = content;
    }


    @Override
    public LayoutComponent<?, ?> getPanel() {
        return this.pageComponent;
    }

    @Override
    public ChunkGenerator updateSettings(
            ChunkGenerator netherGenerator
    ) {
        if (netherGenerator.getBiomeSource() instanceof BiomeSourceWithConfig<?, ?> bsc) {
            if (bsc.getBiomeSourceConfig() instanceof WoverNetherConfig) {
                @SuppressWarnings("unchecked") final BiomeSourceWithConfig<?, WoverNetherConfig> biomeSource = (BiomeSourceWithConfig<?, WoverNetherConfig>) bsc;

                WoverNetherConfig netherConfig = new WoverNetherConfig(
                        netherLegacy.isChecked()
                                ? WoverNetherConfig.NetherBiomeMapType.SQUARE
                                : WoverNetherConfig.NetherBiomeMapType.HEX,
                        netherBiomeSize.getValue() * 16,
                        netherVerticalBiomeSize.getValue() * 16,
                        netherVertical.isChecked(),
                        netherAmplified.isChecked()
                );

                Map<ResourceKey<LevelStem>, ChunkGenerator> betterxDimensions = DimensionsWrapper.getDimensionsMap(
                        PresetsRegistry.WOVER_WORLD);
                Map<ResourceKey<LevelStem>, ChunkGenerator> betterxAmplifiedDimensions = DimensionsWrapper.getDimensionsMap(
                        PresetsRegistry.WOVER_WORLD_AMPLIFIED);
                final Registry<NoiseGeneratorSettings> noiseSettings = WorldState.allStageRegistryAccess()
                                                                                 .registryOrThrow(Registries.NOISE_SETTINGS);

                ChunkGenerator referenceGenerator = (
                        netherAmplified.isChecked()
                                ? betterxAmplifiedDimensions
                                : betterxDimensions
                ).get(LevelStem.NETHER);

                if (referenceGenerator instanceof WoverChunkGenerator woverChunkGenerator) {
                    netherGenerator = new WoverChunkGenerator(
                            netherGenerator.getBiomeSource(),
                            woverChunkGenerator.generatorSettings()
                    );
                } else {
                    netherGenerator = new WoverChunkGenerator(
                            netherGenerator.getBiomeSource(),
                            netherAmplified.isChecked()
                                    ? noiseSettings.getHolderOrThrow(WoverChunkGenerator.AMPLIFIED_NETHER)
                                    : noiseSettings.getHolderOrThrow(NoiseGeneratorSettings.NETHER)
                    );
                }
                if (netherGenerator instanceof ConfiguredChunkGenerator cfg) {
                    cfg.wover_setConfiguredWorldPreset(netherAmplified.isChecked()
                            ? PresetsRegistry.WOVER_WORLD_AMPLIFIED
                            : PresetsRegistry.WOVER_WORLD
                    );
                }

                biomeSource.setBiomeSourceConfig(netherConfig);
            }
        }

        return netherGenerator;
    }
}
