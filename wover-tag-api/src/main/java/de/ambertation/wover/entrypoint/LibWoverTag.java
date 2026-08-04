package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.impl.AutoBiomeTagProvider;
import de.ambertation.wover.datagen.impl.AutoBlockTagProvider;
import de.ambertation.wover.datagen.impl.AutoItemTagProvider;
import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.tag.api.predefined.*;
import de.ambertation.wover.tag.impl.TagBootstrapContextImpl;

import net.fabricmc.api.ModInitializer;

import static de.ambertation.wover.events.impl.AbstractEvent.SYSTEM_PRIORITY;

public class LibWoverTag implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-tag", "wover");

    @Override
    public void onInitialize() {
        WoverDataGenEntryPoint.registerAutoProvider(AutoBlockTagProvider::new);
        WoverDataGenEntryPoint.registerAutoProvider(AutoItemTagProvider::new);
        WoverDataGenEntryPoint.registerAutoProvider(AutoBiomeTagProvider::new);

        CommonBiomeTags.ensureStaticallyLoaded();
        CommonBlockTags.ensureStaticallyLoaded();
        CommonItemTags.ensureStaticallyLoaded();
        CommonPoiTags.ensureStaticallyLoaded();

        MineableTags.ensureStaticallyLoaded();
        ToolTags.ensureStaticallyLoaded();

        WorldLifecycle
                .BEFORE_LOADING_RESOURCES
                .subscribe((resourceManager, featureFlagSet) -> TagBootstrapContextImpl.invalidateCaches(), SYSTEM_PRIORITY);
    }
}