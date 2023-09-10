package org.betterx.wover.generator.impl.client;

import de.ambertation.wunderlib.ui.layout.components.Checkbox;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.Range;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Value;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkGenerator;

import org.jetbrains.annotations.NotNull;

public class EndConfigPage implements BiomeSourceConfigPanel<WoverEndBiomeSource, WoverEndConfig> {
    private final Checkbox endLegacy;
    private final Checkbox endCustomTerrain;
    private final Checkbox generateEndVoid;

    private final Range<Integer> landBiomeSize;
    private final Range<Integer> voidBiomeSize;
    private final Range<Integer> centerBiomeSize;
    private final Range<Integer> barrensBiomeSize;
    private final Range<Integer> innerRadius;
    private final VerticalStack pageComponent;

    public EndConfigPage(@NotNull WoverEndConfig endConfig) {
        VerticalStack content = new VerticalStack(Value.fill(), Value.fit()).centerHorizontal();
        content.addSpacer(8);

        endLegacy = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.legacy_square"),
                endConfig.mapVersion == WoverEndConfig.EndBiomeMapType.SQUARE
        );

        endCustomTerrain = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.custom_end_terrain"),
                endConfig.generatorVersion != WoverEndConfig.EndBiomeGeneratorType.VANILLA
        );

        generateEndVoid = content.addCheckbox(
                Value.fit(), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.end_void"),
                endConfig.withVoidBiomes
        );

        content.addSpacer(12);
        content.addText(Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.avg_biome_size"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        landBiomeSize = content.addRange(
                Value.fixed(200), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.land_biome_size"),
                1, 512, endConfig.landBiomesSize / 16
        );

        voidBiomeSize = content.addRange(
                Value.fixed(200), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.void_biome_size"),
                1, 512, endConfig.voidBiomesSize / 16
        );

        centerBiomeSize = content.addRange(
                Value.fixed(200), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.center_biome_size"),
                1, 512, endConfig.centerBiomesSize / 16
        );

        barrensBiomeSize = content.addRange(
                Value.fixed(200), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.barrens_biome_size"),
                1, 512, endConfig.barrensBiomesSize / 16
        );

        content.addSpacer(12);
        content.addText(Value.fit(), Value.fit(), Component.translatable("title.screen.wover.worldgen.other"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        innerRadius = content.addRange(
                Value.fixed(200), Value.fit(),
                Component.translatable("title.screen.wover.worldgen.central_radius"),
                1, 512, (int) Math.sqrt(endConfig.innerVoidRadiusSquared) / 16
        );

        endCustomTerrain.onChange((cb, state) -> {
            landBiomeSize.setEnabled(state);
            voidBiomeSize.setEnabled(state && generateEndVoid.isChecked());
            centerBiomeSize.setEnabled(state);
            barrensBiomeSize.setEnabled(state);
        });

        generateEndVoid.onChange((cb, state) -> voidBiomeSize.setEnabled(state && endCustomTerrain.isChecked()));

        content.addSpacer(8);
        this.pageComponent = content.setDebugName("End Page");
    }

    @Override
    public LayoutComponent<?, ?> getPanel() {
        return pageComponent;
    }

    @Override
    public ChunkGenerator updateSettings(ChunkGenerator endGenerator) {
        if (endGenerator.getBiomeSource() instanceof BiomeSourceWithConfig<?, ?> bsc) {
            if (bsc.getBiomeSourceConfig() instanceof WoverEndConfig) {
                @SuppressWarnings("unchecked") final BiomeSourceWithConfig<?, WoverEndConfig> biomeSource = (BiomeSourceWithConfig<?, WoverEndConfig>) bsc;

                WoverEndConfig endConfig = new WoverEndConfig(
                        endLegacy.isChecked()
                                ? WoverEndConfig.EndBiomeMapType.SQUARE
                                : WoverEndConfig.EndBiomeMapType.HEX,
                        endCustomTerrain.isChecked()
                                ? WoverEndConfig.EndBiomeGeneratorType.PAULEVS
                                : WoverEndConfig.EndBiomeGeneratorType.VANILLA,
                        generateEndVoid.isChecked(),
                        (int) Math.pow(innerRadius.getValue() * 16, 2),
                        centerBiomeSize.getValue() * 16,
                        voidBiomeSize.getValue() * 16,
                        landBiomeSize.getValue() * 16,
                        barrensBiomeSize.getValue() * 16
                );

                biomeSource.setBiomeSourceConfig(endConfig);
            }
        }

        return endGenerator;
    }
}
