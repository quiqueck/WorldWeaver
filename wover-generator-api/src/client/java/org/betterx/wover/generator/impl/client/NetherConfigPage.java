package org.betterx.wover.generator.impl.client;

import de.ambertation.wunderlib.ui.layout.components.Checkbox;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.Range;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Value;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.client.biomesource.client.BiomeSourceConfigPanel;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.chunk.ChunkGenerator;

import org.jetbrains.annotations.NotNull;

public class NetherConfigPage implements BiomeSourceConfigPanel<WoverNetherBiomeSource, WoverNetherConfig> {
    private final Checkbox netherLegacy;
    private final Checkbox netherVertical;

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

        netherVertical.onChange((checkbox, selected) -> netherVerticalBiomeSize.setEnabled(selected));

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
                        netherVertical.isChecked()
                );

                biomeSource.setBiomeSourceConfig(netherConfig);
            }
        }

        return netherGenerator;
    }
}
