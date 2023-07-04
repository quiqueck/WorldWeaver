package org.betterx.wover.datagen.api;

import net.minecraft.core.RegistrySetBuilder;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.util.LinkedList;
import java.util.List;

public abstract class WoverDataGenEntryPoint implements DataGeneratorEntrypoint {
    private List<WoverRegistryProvider<?>> registryProviders = null;

    protected <T> void addRegistryProvider(WoverRegistryProvider<T> provider) {
        registryProviders.add(provider);
    }

    protected abstract void onInitialize();

    protected void initialize() {
        if (registryProviders == null) {
            registryProviders = new LinkedList<>();
            onInitialize();
        }
    }

    protected void onAddProviders(FabricDataGenerator.Pack pack) {
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        initialize();

        registryProviders.forEach(provider -> pack.addProvider(provider::getProvider));
        onAddProviders(pack);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
        initialize();

        registryProviders.forEach(provider -> provider.buildRegistry(registryBuilder));
    }
}
