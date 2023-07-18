package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.predefined.*;

import net.fabricmc.api.ModInitializer;

public class WoverTag implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-tag", "wover");

    @Override
    public void onInitialize() {
        CommonBiomeTags.ensureStaticallyLoaded();
        CommonBlockTags.ensureStaticallyLoaded();
        CommonItemTags.ensureStaticallyLoaded();
        CommonPoiTags.ensureStaticallyLoaded();

        MineableTags.ensureStaticallyLoaded();
        ToolTags.ensureStaticallyLoaded();
    }
}