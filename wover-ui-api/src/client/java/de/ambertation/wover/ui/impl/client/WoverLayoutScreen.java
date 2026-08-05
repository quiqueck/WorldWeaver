package de.ambertation.wover.ui.impl.client;

import de.ambertation.wunderlib.ui.vanilla.LayoutScreenWithIcon;
import de.ambertation.wover.entrypoint.LibWoverUi;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.NotNull;

public abstract class WoverLayoutScreen extends LayoutScreenWithIcon {
    public static final Identifier WOVER_LOGO_LOCATION = LibWoverUi.C.id("icon.png");
    public static final Identifier WOVER_LOGO_WHITE_LOCATION = LibWoverUi.C.id("icon_bright.png");

    public WoverLayoutScreen(
            Component component
    ) {
        super(WOVER_LOGO_LOCATION, component);
    }

    public WoverLayoutScreen(
            @NotNull Runnable onClose,
            Component component
    ) {
        super(onClose, WOVER_LOGO_LOCATION, component);
    }

    public WoverLayoutScreen(
            @NotNull Runnable onClose,
            Component component,
            int topPadding,
            int bottomPadding,
            int sidePadding
    ) {
        super(onClose, WOVER_LOGO_LOCATION, component, topPadding, bottomPadding, sidePadding);
    }
}
