package org.betterx.wover.ui.impl.client;

import de.ambertation.wunderlib.ui.ColorHelper;
import de.ambertation.wunderlib.ui.layout.components.*;
import de.ambertation.wunderlib.ui.layout.values.Size;
import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.entrypoint.WoverUi;
import org.betterx.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class WelcomeScreen extends WoverLayoutScreen {
    public static final ResourceLocation BETTERX_LOCATION = WoverUi.C.id("betterx.png");
    public static final ResourceLocation BACKGROUND = WoverUi.C.id("header.jpg");
    public static final ResourceLocation ICON_BETTERNETHER = WoverUi.C.id("icon_betternether.png");
    public static final ResourceLocation ICON_BETTEREND = WoverUi.C.id("icon_betterend.png");
    public static final ResourceLocation ICON_BCLIB = WoverUi.C.id("icon_bclib.png");

    public WelcomeScreen(Screen parent) {
        super(parent, translatable("wover.welcome.title"));
    }

    @Override
    protected LayoutComponent<?, ?> initContent() {
        VerticalStack content = new VerticalStack(fill(), fit()).setDebugName("content");

        content.addImage(fill(), fit(), BACKGROUND, new Size(854 / 2, 200 / 2));
        content.addHorizontalLine(1).setColor(ColorHelper.BLACK);
        content.addSpacer(16);
        HorizontalStack headerRow = content.addRow(fit(), fit()).setDebugName("title bar").centerHorizontal();
        headerRow.addIcon(icon, Size.of(512)).setDebugName("icon");
        headerRow.addSpacer(4);
        headerRow.addText(fit(), fit(), title).centerHorizontal().setColor(ColorHelper.WHITE).setDebugName("title");
        headerRow.addImage(fixed(178 / 2), fixed(40 / 2), BETTERX_LOCATION, Size.of(178, 40)).setDebugName("betterx");
        content.addSpacer(16);

        content.addMultilineText(fill(), fit(), MultiLineText.parse(translatable("wover.welcome.description")))
               .centerHorizontal();

        Container padContainer = new Container(fill(), fit()).setPadding(10, 0, 10, 10).setDebugName("padContainer");
        VerticalStack innerContent = new VerticalStack(fill(), fit()).setDebugName("innerContent");
        padContainer.addChild(innerContent);
        content.add(padContainer);

        addSeparator(innerContent, ICON_BETTERNETHER);

        // Do Update Checks
        Checkbox check = innerContent.addCheckbox(
                                             fit(), fit(),
                                             translatable("title.config.wover.client.version.check"),
                                             ClientConfigs.CLIENT.checkForNewVersions.get()
                                     )
                                     .onChange((cb, state) -> ClientConfigs.CLIENT.checkForNewVersions.set(state));
        innerContent.addSpacer(2);
        HorizontalStack dscBox = innerContent.indent(24);
        dscBox.addMultilineText(fill(), fit(), translatable("description.config.wover.client.version.check"))
              .setColor(ColorHelper.GRAY);
        dscBox.addSpacer(8);

        // Hide Experimental Dialog
        innerContent.addSpacer(8);
        Checkbox experimental = innerContent.addCheckbox(
                                                    fit(), fit(),
                                                    translatable("title.config.wover.client.ui.suppressExperimentalDialogOnLoad"),
                                                    ClientConfigs.CLIENT.disableExperimentalWarning.get()
                                            )
                                            .onChange((cb, state) -> ClientConfigs.CLIENT.disableExperimentalWarning.set(
                                                    state));
        innerContent.addSpacer(2);
        dscBox = innerContent.indent(24);
        dscBox.addMultilineText(
                      fill(),
                      fit(),
                      translatable("description.config.wover.client.ui.suppressExperimentalDialogOnLoad")
              )
              .setColor(ColorHelper.GRAY);
        dscBox.addSpacer(8);

        // Use BetterX WorldType
        innerContent.addSpacer(8);
        Checkbox betterx = innerContent.addCheckbox(
                                               fit(), fit(),
                                               translatable("title.config.wover.client.ui.forceBetterXPreset"),
                                               ClientConfigs.CLIENT.forceBetterXPreset.get()
                                       )
                                       .onChange((cb, state) -> ClientConfigs.CLIENT.forceBetterXPreset.set(state));
        innerContent.addSpacer(2);
        dscBox = innerContent.indent(24);
        dscBox.addMultilineText(
                      fill(), fit(),
                      translatable("warning.config.wover.client.ui.forceBetterXPreset")
                              .setStyle(Style.EMPTY
                                      .withBold(true)
                                      .withColor(ColorHelper.RED)
                              )
                              .append(translatable(
                                      "description.config.wover.client.ui.forceBetterXPreset").setStyle(
                                      Style.EMPTY
                                              .withBold(false)
                                              .withColor(ColorHelper.GRAY))
                              )
              )
              .setColor(ColorHelper.GRAY);
        dscBox.addSpacer(8);

        innerContent.addSpacer(16);
        innerContent.addButton(fit(), fit(), CommonComponents.GUI_PROCEED).onPress((bt) -> {
            ClientConfigs.CLIENT.didPresentWelcomeScreen.set(true);
            ClientConfigs.CLIENT.checkForNewVersions.set(check.isChecked());
            ClientConfigs.CLIENT.disableExperimentalWarning.set(experimental.isChecked());
            ClientConfigs.CLIENT.forceBetterXPreset.set(betterx.isChecked());
            ClientConfigs.CLIENT.save();

            ClientWorldLifecycleImpl.AFTER_WELCOME_SCREEN.emit(c -> c.didPresent());

            //TODO: VersionChecker needs to be started here
            //VersionChecker.startCheck(true);
            onClose();
        }).alignRight();

        return VerticalScroll.create(fill(), fill(), content).setScrollerPadding(0);
    }

    private void addSeparator(VerticalStack innerContent, ResourceLocation image) {
        final int sepWidth = (int) (427 / 1.181) / 2;
        HorizontalStack separator = new HorizontalStack(fit(), fit()).centerHorizontal();
        separator.addHLine(fixed((sepWidth - 32) / 2), fixed(32)).centerVertical();
        separator.addSpacer(1);
        separator.addImage(fixed(32), fixed(32), image, Size.of(64)).alignBottom();
        separator.addHLine(fixed((sepWidth - 32) / 2), fixed(32)).centerVertical();
        innerContent.addSpacer(16);
        innerContent.add(separator);
        innerContent.addSpacer(4);
    }

    @Override
    protected LayoutComponent<?, ?> createScreen(LayoutComponent<?, ?> content) {
        return content;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, width, height, 0xBD343444);
//        Rectangle BANNER_UV = new Rectangle(0, 0, 427, 100);
//        Size BANNER_RESOURCE_SIZE = BANNER_UV.size();
//        Size BANNER_SIZE = BANNER_UV.sizeFromWidth(this.width);
//
//        RenderHelper.renderImage(
//                poseStack,
//                BANNER_SIZE.width(),
//                BANNER_SIZE.height(),
//                BACKGROUND,
//                BANNER_UV,
//                BANNER_RESOURCE_SIZE,
//                1.0f
//        );
    }
}
