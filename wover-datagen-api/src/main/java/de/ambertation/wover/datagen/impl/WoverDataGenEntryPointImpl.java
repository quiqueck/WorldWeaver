package de.ambertation.wover.datagen.impl;

import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverAutoProvider;

import net.minecraft.data.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class WoverDataGenEntryPointImpl {
    private static List<PackBuilder.ProviderFactory<?>> AUTO_PROVIDERS = new ArrayList<>(3);

    public static <T extends DataProvider> void registerAutoProvider(PackBuilder.ProviderFactory<T> providerFactory) {
        AUTO_PROVIDERS.add(providerFactory);
    }

    public static void addDefaultGlobalProviders(PackBuilderImpl pack) {
        AUTO_PROVIDERS.stream()
                      .map(provider -> provider.create(pack.modCore()))
                      .map(provider -> {
                          if (!(provider instanceof WoverAutoProvider)) {
                              throw new IllegalArgumentException("Auto Providers must implement WoverAutoProvider: " + provider.getClass());
                          }
                          return provider;
                      })
                      //make sure that providers with redirectors are instatiated first
                      .sorted((a, b) -> {
                          if (a instanceof WoverAutoProvider.WithRedirect) {
                              return b instanceof WoverAutoProvider.WithRedirect ? 0 : -1;
                          } else {
                              return b instanceof WoverAutoProvider.WithRedirect ? 1 : 0;
                          }
                      })
                      .forEach(pack::instantiateAutoProvider);
    }
}
