package org.betterx.wover.generator.impl.client;

import de.ambertation.wunderlib.ui.layout.components.*;
import de.ambertation.wunderlib.ui.layout.components.render.RenderHelper;
import de.ambertation.wunderlib.ui.layout.values.Rectangle;
import de.ambertation.wunderlib.ui.layout.values.Size;
import de.ambertation.wunderlib.ui.vanilla.LayoutScreen;
import org.betterx.wover.generator.api.biomesource.end.WoverEndConfig;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherConfig;
import org.betterx.wover.generator.api.preset.PresetsRegistry;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.chunkgenerator.DimensionsWrapper;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGeneratorImpl;
import org.betterx.wover.preset.api.SortableWorldPreset;
import org.betterx.wover.ui.impl.client.WelcomeScreen;
import org.betterx.wover.ui.impl.client.WoverLayoutScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WorldSetupScreen extends LayoutScreen {
    private final WorldCreationContext context;
    private final CreateWorldScreen createWorldScreen;
    private Range<Integer> netherBiomeSize;
    private Range<Integer> netherVerticalBiomeSize;
    private Range<Integer> landBiomeSize;
    private Range<Integer> voidBiomeSize;
    private Range<Integer> centerBiomeSize;
    private Range<Integer> barrensBiomeSize;
    private Range<Integer> innerRadius;

    public WorldSetupScreen(@Nullable CreateWorldScreen parent, WorldCreationContext context) {
        super(parent, Component.translatable("title.screen.wover.worldgen.main"), 10, 10, 10);
        this.context = context;
        this.createWorldScreen = parent;
    }


    private Checkbox woverEnd;
    private Checkbox woverNether;
    Checkbox endLegacy;
    Checkbox endCustomTerrain;
    Checkbox generateEndVoid;
    Checkbox netherLegacy;
    Checkbox netherVertical;
    Checkbox netherAmplified;

    public LayoutComponent<?, ?> netherPage(WoverNetherConfig netherConfig) {
        VerticalStack content = new VerticalStack(fill(), fit()).centerHorizontal();
        content.addSpacer(8);

        woverNether = content.addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.custom_nether_biome_source"),
                netherConfig.mapVersion != WoverNetherConfig.NetherBiomeMapType.VANILLA
        );

        netherLegacy = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.legacy_square"),
                netherConfig.mapVersion == WoverNetherConfig.NetherBiomeMapType.SQUARE
        );

        netherAmplified = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.nether_amplified"),
                netherConfig.amplified
        );

        netherVertical = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.nether_vertical"),
                netherConfig.useVerticalBiomes
        );

        content.addSpacer(12);
        content.addText(fit(), fit(), Component.translatable("title.screen.wover.worldgen.avg_biome_size"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        netherBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.nether_biome_size"),
                1,
                512,
                netherConfig.biomeSize / 16
        );

        netherVerticalBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.nether_vertical_biome_size"),
                1,
                32,
                netherConfig.biomeSizeVertical / 16
        );

        woverNether.onChange((cb, state) -> {
            netherLegacy.setEnabled(state);
            netherAmplified.setEnabled(state);
            netherVertical.setEnabled(state);
            netherBiomeSize.setEnabled(state);
            netherVerticalBiomeSize.setEnabled(state && netherVertical.isChecked());
        });

        netherVertical.onChange((cb, state) -> netherVerticalBiomeSize.setEnabled(state && woverNether.isChecked()));

        content.addSpacer(8);
        return content.setDebugName("Nether page");
    }

    public LayoutComponent<?, ?> endPage(WoverEndConfig endConfig) {
        VerticalStack content = new VerticalStack(fill(), fit()).centerHorizontal();
        content.addSpacer(8);
        woverEnd = content.addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.custom_end_biome_source"),
                endConfig.mapVersion != WoverEndConfig.EndBiomeMapType.VANILLA
        );

        endLegacy = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.legacy_square"),
                endConfig.mapVersion == WoverEndConfig.EndBiomeMapType.SQUARE
        );

        endCustomTerrain = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.custom_end_terrain"),
                endConfig.generatorVersion != WoverEndConfig.EndBiomeGeneratorType.VANILLA
        );

        generateEndVoid = content.indent(20).addCheckbox(
                fit(), fit(),
                Component.translatable("title.screen.wover.worldgen.end_void"),
                endConfig.withVoidBiomes
        );

        content.addSpacer(12);
        content.addText(fit(), fit(), Component.translatable("title.screen.wover.worldgen.avg_biome_size"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        landBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.land_biome_size"),
                1,
                512,
                endConfig.landBiomesSize / 16
        );

        voidBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.void_biome_size"),
                1,
                512,
                endConfig.voidBiomesSize / 16
        );

        centerBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.center_biome_size"),
                1,
                512,
                endConfig.centerBiomesSize / 16
        );

        barrensBiomeSize = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.barrens_biome_size"),
                1,
                512,
                endConfig.barrensBiomesSize / 16
        );

        content.addSpacer(12);
        content.addText(fit(), fit(), Component.translatable("title.screen.wover.worldgen.other"))
               .centerHorizontal();
        content.addHorizontalSeparator(8).alignTop();

        innerRadius = content.addRange(
                fixed(200),
                fit(),
                Component.translatable("title.screen.wover.worldgen.central_radius"),
                1,
                512,
                (int) Math.sqrt(endConfig.innerVoidRadiusSquared) / 16
        );


        woverEnd.onChange((cb, state) -> {
            endLegacy.setEnabled(state);
            endCustomTerrain.setEnabled(state);
            generateEndVoid.setEnabled(state);

            landBiomeSize.setEnabled(state && endCustomTerrain.isChecked());
            voidBiomeSize.setEnabled(state && endCustomTerrain.isChecked() && generateEndVoid.isChecked());
            centerBiomeSize.setEnabled(state && endCustomTerrain.isChecked());
            barrensBiomeSize.setEnabled(state && endCustomTerrain.isChecked());
        });

        endCustomTerrain.onChange((cb, state) -> {
            landBiomeSize.setEnabled(state);
            voidBiomeSize.setEnabled(state && generateEndVoid.isChecked());
            centerBiomeSize.setEnabled(state);
            barrensBiomeSize.setEnabled(state);
        });

        generateEndVoid.onChange((cb, state) -> voidBiomeSize.setEnabled(state && endCustomTerrain.isChecked()));

        content.addSpacer(8);
        return content.setDebugName("End Page");
    }

    private void updateSettings() {
        Map<ResourceKey<LevelStem>, ChunkGenerator> betterxDimensions = DimensionsWrapper.getDimensionsMap(
                PresetsRegistry.WOVER_WORLD);
        Map<ResourceKey<LevelStem>, ChunkGenerator> betterxAmplifiedDimensions = DimensionsWrapper.getDimensionsMap(
                PresetsRegistry.WOVER_WORLD_AMPLIFIED);
        Map<ResourceKey<LevelStem>, ChunkGenerator> vanillaDimensions = DimensionsWrapper.getDimensionsMap(
                WorldPresets.NORMAL);
        WoverEndConfig.EndBiomeMapType endVersion = WoverEndConfig.DEFAULT.mapVersion;


        if (woverEnd.isChecked()) {
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

            ChunkGenerator endGenerator = betterxDimensions.get(LevelStem.END);
            ((WoverEndBiomeSource) endGenerator.getBiomeSource()).setBiomeSourceConfig(endConfig);

            updateConfiguration(LevelStem.END, BuiltinDimensionTypes.END, endGenerator);
        } else {
            ChunkGenerator endGenerator = vanillaDimensions.get(LevelStem.END);
            updateConfiguration(LevelStem.END, BuiltinDimensionTypes.END, endGenerator);
        }

        if (woverNether.isChecked()) {
            WoverNetherConfig netherConfig = new WoverNetherConfig(
                    netherLegacy.isChecked()
                            ? WoverNetherConfig.NetherBiomeMapType.SQUARE
                            : WoverNetherConfig.NetherBiomeMapType.HEX,
                    netherBiomeSize.getValue() * 16,
                    netherVerticalBiomeSize.getValue() * 16,
                    netherVertical.isChecked(),
                    netherAmplified.isChecked()
            );

            ChunkGenerator netherGenerator = (
                    netherAmplified.isChecked()
                            ? betterxAmplifiedDimensions
                            : betterxDimensions
            ).get(LevelStem.NETHER);
            ((WoverNetherBiomeSource) netherGenerator.getBiomeSource()).setBiomeSourceConfig(netherConfig);

            updateConfiguration(LevelStem.NETHER, BuiltinDimensionTypes.NETHER, netherGenerator);
        } else {
            ChunkGenerator endGenerator = vanillaDimensions.get(LevelStem.NETHER);
            updateConfiguration(LevelStem.NETHER, BuiltinDimensionTypes.NETHER, endGenerator);
        }

        final WorldCreationUiState acc = createWorldScreen.getUiState();
        final Holder<WorldPreset> configuredPreset = acc.getWorldType().preset();
        if (configuredPreset != null && configuredPreset.value() instanceof SortableWorldPreset worldPreset) {
            ResourceKey<WorldPreset> key = configuredPreset.unwrapKey().orElse(null);
            if (key == null) key = worldPreset.parentKey();

            acc.setWorldType(new WorldCreationUiState.WorldTypeEntry(Holder.direct(
                    worldPreset.withDimensions(
                            createWorldScreen
                                    .getUiState()
                                    .getSettings()
                                    .selectedDimensions()
                                    .dimensions(),
                            key
                    )
            )));
        }
    }


    private void updateConfiguration(
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            ChunkGenerator chunkGenerator
    ) {
        createWorldScreen.getUiState().updateDimensions(
                (registryAccess, worldDimensions) -> new WorldDimensions(WoverChunkGeneratorImpl.replaceGenerator(
                        dimensionKey,
                        dimensionTypeKey,
                        registryAccess,
                        worldDimensions.dimensions(),
                        chunkGenerator
                ))
        );
    }

    @Override
    protected LayoutComponent<?, ?> createScreen(LayoutComponent<?, ?> content) {
        VerticalStack rows = new VerticalStack(fill(), fill()).setDebugName("title stack");

        if (topPadding > 0) rows.addSpacer(topPadding);
        rows.add(content);
        if (bottomPadding > 0) rows.addSpacer(bottomPadding);

        if (sidePadding <= 0) return rows;

        HorizontalStack cols = new HorizontalStack(fill(), fill()).setDebugName("padded side");
        cols.addSpacer(sidePadding);
        cols.add(rows);
        cols.addSpacer(sidePadding);
        return cols;
    }

    Button netherButton, endButton;
    VerticalScroll<?> scroller;
    HorizontalStack title;

    @Override
    protected LayoutComponent<?, ?> initContent() {
        WoverEndConfig endConfig = WoverEndConfig.VANILLA;
        WoverNetherConfig netherConfig = WoverNetherConfig.VANILLA;

        final WorldCreationUiState acc = createWorldScreen.getUiState();
        final Holder<WorldPreset> configuredPreset = acc.getWorldType().preset();
        if (configuredPreset.value() instanceof SortableWorldPreset wp) {
            LevelStem endStem = wp.getDimension(LevelStem.END);
            if (endStem != null && endStem.generator().getBiomeSource() instanceof WoverEndBiomeSource bs) {
                endConfig = bs.getBiomeSourceConfig();
            }
            LevelStem netherStem = wp.getDimension(LevelStem.NETHER);
            if (netherStem != null && netherStem.generator().getBiomeSource() instanceof WoverNetherBiomeSource bs) {
                netherConfig = bs.getBiomeSourceConfig();
            }
        }

        LayoutComponent<?, ? extends LayoutComponent<?, ?>> netherPage = netherPage(netherConfig);
        LayoutComponent<?, ? extends LayoutComponent<?, ?>> endPage = endPage(endConfig);

        Tabs main = new Tabs(fill(), fill()).setPadding(8, 0, 0, 0);
        main.addPage(Component.translatable("title.wover.the_nether"), VerticalScroll.create(netherPage));
        main.addSpacer(8);
        main.addPage(Component.translatable("title.wover.the_end"), scroller = VerticalScroll.create(endPage));
        netherButton = main.getButton(0);
        endButton = main.getButton(1);

        title = new HorizontalStack(fit(), fit()).setDebugName("title bar").alignBottom();
        title.addImage(fixed(22), fixed(22), WoverLayoutScreen.WOVER_LOGO_WHITE_LOCATION, Size.of(256))
             .setDebugName("icon");
        title.addSpacer(4);
        VerticalStack logos = title.addColumn(fit(), fit());
        logos.addImage(fixed(178 / 3), fixed(40 / 3), WelcomeScreen.BETTERX_LOCATION, Size.of(178, 40));
        logos.add(super.createTitle());
        logos.addSpacer(2);

        main.addFiller();
        main.addComponent(title);


        VerticalStack rows = new VerticalStack(fill(), fill());
        rows.add(main);
        rows.addSpacer(4);
        rows.addButton(fit(), fit(), CommonComponents.GUI_DONE).onPress((bt) -> {
            updateSettings();
            onClose();
        }).alignRight();

        main.onPageChange((tabs, idx) -> targetT = 1 - idx);

        return rows;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, width, height, 0xBD343444);
    }

    record IconState(int left, int top, int size) {
        //easing curves from https://easings.net/de
        static double easeInOutQuint(double t) {
            return t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2;
        }

        static double easeOutBounce(double x) {
            final double n1 = 7.5625;
            final double d1 = 2.75;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5 / d1) * x + 0.75;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25 / d1) * x + 0.9375;
            } else {
                return n1 * (x -= 2.625 / d1) * x + 0.984375;
            }
        }

        static int lerp(double t, int x0, int x1) {
            return (int) ((1 - t) * x0 + t * x1);
        }
    }

    IconState netherOff, netherOn, endOff, endOn;
    double iconT = 0.5;
    double targetT = 1;

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        final double SPEED = 0.05;
        if (targetT < iconT && iconT > 0) iconT = Math.max(0, iconT - f * SPEED);
        else if (targetT > iconT && iconT < 1) iconT = Math.min(1, iconT + f * SPEED);

        final double t;
        if (iconT > 0 && iconT < 1) {
            if (targetT > iconT) {
                t = IconState.easeOutBounce(iconT);
            } else {
                t = 1 - IconState.easeOutBounce(1 - iconT);
            }
        } else t = iconT;

        if (endButton != null) {
            if (endOff == null) {
                endOff = new IconState(
                        endButton.getScreenBounds().right() - 12,
                        endButton.getScreenBounds().top - 7,
                        16
                );
                endOn = new IconState(
                        (title.getScreenBounds().left - endButton.getScreenBounds().right()) / 2
                                + endButton.getScreenBounds().right()
                                - 14,
                        scroller.getScreenBounds().top - 16,
                        32
                );
            }
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(
                    IconState.lerp(t, endOn.left, endOff.left),
                    IconState.lerp(t, endOn.top, endOff.top),
                    0
            );
            int size = IconState.lerp(t, endOn.size, endOff.size);
            RenderHelper.renderImage(
                    guiGraphics, 0, 0,
                    size,
                    size,
                    WelcomeScreen.ICON_BETTEREND,
                    Size.of(32), new Rectangle(0, 0, 32, 32),
                    (float) 1
            );
            guiGraphics.pose().popPose();
        }

        if (netherButton != null) {
            if (netherOff == null) {
                netherOff = new IconState(
                        netherButton.getScreenBounds().right() - 12,
                        netherButton.getScreenBounds().top - 7,
                        16
                );
                netherOn = endOn;
            }
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(
                    IconState.lerp(t, netherOff.left, netherOn.left),
                    IconState.lerp(t, netherOff.top, netherOn.top),
                    0
            );
            int size = IconState.lerp(t, netherOff.size, netherOn.size);
            RenderHelper.renderImage(
                    guiGraphics, 0, 0,
                    size,
                    size,
                    WelcomeScreen.ICON_BETTERNETHER,
                    Size.of(32), new Rectangle(0, 0, 32, 32),
                    (float) 1
            );
            guiGraphics.pose().popPose();
        }
    }
}
