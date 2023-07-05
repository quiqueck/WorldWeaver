package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class WoverDataGenEntryPoint implements DataGeneratorEntrypoint {
    public interface DatapackBootstrap {
        void bootstrap(
                FabricDataGenerator fabricDataGenerator,
                FabricDataGenerator.Pack pack,
                ResourceLocation location
        );
    }

    private List<WoverRegistryProvider<?>> registryProviders = null;
    private Map<ResourceLocation, DatapackBootstrap> datapackProviders = null;

    protected <T> void addRegistryProvider(WoverRegistryProvider<T> provider) {
        registryProviders.add(provider);
    }

    protected void addDatapackProvider(ResourceLocation location, DatapackBootstrap bootstrap) {
        datapackProviders.put(location, bootstrap);
    }

    protected abstract void onInitializeProviders();

    protected abstract ModCore modCore();

    protected void initialize() {
        if (registryProviders == null) {
            registryProviders = new LinkedList<>();
            datapackProviders = new HashMap<>();
            onInitializeProviders();
        }


    }

    protected void onInitializeDataGenerator(
            FabricDataGenerator fabricDataGenerator,
            FabricDataGenerator.Pack globalPack
    ) {
    }

    protected FabricDataGenerator.Pack createBuiltinDatapack(
            FabricDataGenerator generator,
            ResourceLocation location
    ) {
        FabricDataGenerator.Pack pack = generator.createBuiltinResourcePack(location);

        //add a pack description
        pack.addProvider(
                (FabricDataOutput packOutput) -> PackMetadataGenerator.forFeaturePack(
                        packOutput,
                        Component.translatable("pack." + location.getNamespace() + "." + location.getPath() + ".description")
                )
        );

        //make sure to call all registry providers for the pack
        registryProviders
                .stream()
                .filter(p -> p.shouldAddToPack(location))
                .forEach(provider -> pack.addProvider(provider::getProvider));


        return pack;
    }


    @Override
    public final void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        initialize();

        //calls all registry providers for the global pack
        registryProviders.stream()
                         .filter(p -> p.shouldAddToPack(null))
                         .forEach(provider -> pack.addProvider(provider::getProvider));

        //create all datapacks and call their bootstrap methods
        modCore().providedDatapacks().forEach(
                providedLocation -> {
                    final FabricDataGenerator.Pack dataPack = createBuiltinDatapack(
                            fabricDataGenerator,
                            providedLocation
                    );
                    
                    final DatapackBootstrap provider = datapackProviders.get(providedLocation);
                    if (provider != null) {

                        provider.bootstrap(fabricDataGenerator, dataPack, providedLocation);
                    }
                }
        );

        onInitializeDataGenerator(fabricDataGenerator, pack);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
        initialize();

        registryProviders.forEach(provider -> provider.buildRegistry(registryBuilder));
    }

    public interface FabricatedPack {
        <T extends DataProvider> T addProvider(FabricDataGenerator.Pack.Factory<T> factory);

        <T extends DataProvider> T addProvider(FabricDataGenerator.Pack.RegistryDependentFactory<T> factory);

        ResourceLocation getPackId();
    }
}
