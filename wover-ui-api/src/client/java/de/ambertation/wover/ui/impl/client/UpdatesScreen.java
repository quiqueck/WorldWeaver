package de.ambertation.wover.ui.impl.client;

import de.ambertation.wunderlib.ui.ColorHelper;
import de.ambertation.wunderlib.ui.layout.components.HorizontalStack;
import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.layout.values.Size;
import de.ambertation.wunderlib.ui.layout.values.Value;
import de.ambertation.wover.config.api.client.ClientConfigs;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverUi;
import de.ambertation.wover.ui.api.VersionChecker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class UpdatesScreen extends WoverLayoutScreen {
    static final Identifier UPDATE_LOGO_LOCATION = LibWoverUi.C.mk("icon_updater.png");

    public UpdatesScreen(@NotNull Runnable onClose) {
        super(onClose, Component.translatable("wover.updates.title"), 10, 10, 10);
    }

    public UpdatesScreen(Screen parent) {
        super(setScreenOnClose(parent), Component.translatable("wover.updates.title"), 10, 10, 10);
    }

    public static void showUpdateUI() {
        //No more java side render thread calls?
        Minecraft.getInstance().setScreen(new UpdatesScreen(Minecraft.getInstance().screen));
    }

    public Identifier getUpdaterIcon(ModCore core) {
        if (core.namespace.equals(LibWoverUi.C.namespace)) {
            return UPDATE_LOGO_LOCATION;
        }
        ModContainer nfo = core.modContainer;
        if (nfo != null) {
            CustomValue element = nfo.getMetadata().getCustomValue("wover");
            if (element != null) {
                CustomValue.CvObject obj = element.getAsObject();
                if (obj != null) {
                    CustomValue icon = obj.get("updater_icon");
                    return core.mk(icon.getAsString());
                }
            }
        }
        return null;
    }

    @Override
    protected LayoutComponent<?, ?> initContent() {
        VerticalStack rows = new VerticalStack(relative(1), fit()).centerHorizontal();
        rows.addMultilineText(fill(), fit(), Component.translatable("wover.updates.description"))
            .centerHorizontal();

        rows.addSpacer(8);

        VersionChecker.forEachUpdate((mod, cur, updated) -> {
            ModCore core = ModCore.create(mod);
            ModContainer nfo = core.modContainer;
            Identifier icon = getUpdaterIcon(core);
            HorizontalStack row = rows.addRow(fixed(320), fit()).centerHorizontal();
            if (icon != null) {
                row.addImage(Value.fit(), Value.fit(), icon, Size.of(32));
                row.addSpacer(4);
            } else {
                row.addSpacer(36);
            }
            if (nfo != null) {
                row.addText(fit(), fit(), Component.literal(nfo.getMetadata().getName()))
                   .setColor(ColorHelper.WHITE);
            } else {
                row.addText(fit(), fit(), Component.literal(mod)).setColor(ColorHelper.WHITE);
            }
            row.addSpacer(4);
            row.addText(fit(), fit(), Component.literal(cur));
            row.addText(fit(), fit(), Component.literal(" -> "));
            row.addText(fit(), fit(), Component.literal(updated)).setColor(ColorHelper.GREEN);
            row.addFiller();
            boolean createdDownloadLink = false;
            if (nfo != null
                    && nfo.getMetadata().getCustomValue("wover") != null
                    && nfo.getMetadata().getCustomValue("wover").getAsObject().get("downloads") != null) {
                CustomValue.CvObject downloadLinks = nfo.getMetadata()
                                                        .getCustomValue("wover")
                                                        .getAsObject()
                                                        .get("downloads")
                                                        .getAsObject();
                String link = null;
                Component name = null;
                if (ClientConfigs.CLIENT.prefereModrinth.get() && downloadLinks.get("modrinth") != null) {
                    link = downloadLinks.get("modrinth").getAsString();
                    name = Component.translatable("wover.updates.modrinth_link");
                } else if (downloadLinks.get("curseforge") != null) {
                    link = downloadLinks.get("curseforge").getAsString();
                    name = Component.translatable("wover.updates.curseforge_link");
                }

                if (link != null) {
                    createdDownloadLink = true;
                    final String finalLink = link;
                    row.addButton(fit(), fit(), name)
                       .onPress((bt) -> {
                           this.openLink(finalLink);
                       }).centerVertical();
                }
            }

            if (!createdDownloadLink && nfo != null && nfo.getMetadata().getContact().get("homepage").isPresent()) {
                row.addButton(fit(), fit(), Component.translatable("wover.updates.download_link"))
                   .onPress((bt) -> {
                       this.openLink(nfo.getMetadata().getContact().get("homepage").get());
                   }).centerVertical();
            }
        });

        VerticalStack layout = new VerticalStack(relative(1), fill()).centerHorizontal();
        //layout.addSpacer(8);
        layout.addScrollable(rows);
        layout.addSpacer(8);


        HorizontalStack footer = layout.addRow(fill(), fit());
        footer.addFiller();
        footer.addCheckbox(
                      fit(), fit(),
                      Component.translatable("wover.updates.disable_check"),
                      !ClientConfigs.CLIENT.checkForNewVersions.get()
              )
              .onChange((cb, state) -> {
                  ClientConfigs.CLIENT.checkForNewVersions.set(!state);
                  ClientConfigs.CLIENT.save();
              });
        footer.addSpacer(4);
        footer.addButton(fit(), fit(), CommonComponents.GUI_DONE).onPress((bt -> {
            onClose();
        }));
        return layout;
    }

    @Override
    public void renderBackgroundLayer(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, width, height, 0xBD343444);
    }
}
