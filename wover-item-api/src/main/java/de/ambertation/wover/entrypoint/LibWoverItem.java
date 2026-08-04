package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.item.impl.AutoItemRegistryTagProvider;

import net.fabricmc.api.ModInitializer;

public class LibWoverItem implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-item", "wover");

    @Override
    public void onInitialize() {
        //EnchantmentManagerImpl.initialize(); //done in the wover.datapack.registry entrypoint
        WoverDataGenEntryPoint.registerAutoProvider(AutoItemRegistryTagProvider::new);
    }
}