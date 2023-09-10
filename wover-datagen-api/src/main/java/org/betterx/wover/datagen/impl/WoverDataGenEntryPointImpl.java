package org.betterx.wover.datagen.impl;

import org.betterx.wover.datagen.api.PackBuilder;

import net.minecraft.data.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class WoverDataGenEntryPointImpl {
    private static List<PackBuilder.ProviderFactory<?>> AUTO_PROVIDERS = new ArrayList<>(3);

    public static <T extends DataProvider> void registerAutoProvider(PackBuilder.ProviderFactory<T> providerFactory) {
        AUTO_PROVIDERS.add(providerFactory);
    }

    public static void addDefaultGlobalProviders(PackBuilder pack) {
        AUTO_PROVIDERS.forEach(pack::addProvider);
    }
}
