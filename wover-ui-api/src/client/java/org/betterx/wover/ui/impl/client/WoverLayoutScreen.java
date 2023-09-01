package org.betterx.wover.ui.impl.client;

import de.ambertation.wunderlib.ui.vanilla.LayoutScreenWithIcon;
import org.betterx.wover.entrypoint.WoverUi;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public abstract class WoverLayoutScreen extends LayoutScreenWithIcon {
    public static final ResourceLocation WOVER_LOGO_LOCATION = WoverUi.C.id("icon_wover.png");
    public static final ResourceLocation WOVER_LOGO_WHITE_LOCATION = WoverUi.C.id("icon_bright.png");

    public WoverLayoutScreen(
            Component component
    ) {
        super(WOVER_LOGO_LOCATION, component);
    }

    public WoverLayoutScreen(
            @Nullable Screen parent,
            Component component
    ) {
        super(parent, WOVER_LOGO_LOCATION, component);
    }

    public WoverLayoutScreen(
            @Nullable Screen parent,
            Component component,
            int topPadding,
            int bottomPadding,
            int sidePadding
    ) {
        super(parent, WOVER_LOGO_LOCATION, component, topPadding, bottomPadding, sidePadding);
    }
}
