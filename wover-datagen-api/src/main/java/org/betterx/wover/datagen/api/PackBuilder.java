package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A builder for a Datapack. This class is used to manage the content of the Datapack.
 * <p>
 * You can create new Instances of this class using {@link WoverDataGenEntryPoint#addDatapack(ResourceLocation)}
 */
public class PackBuilder {
    /**
     * The {@link ResourceLocation} of the Datapack or {@code null} if it is the global
     * Datapack.
     */
    @Nullable
    public final ResourceLocation location;
    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    @NotNull
    public final ModCore modCore;
    FabricDataGenerator.Pack pack;
    final List<WoverRegistryProvider<?>> registryProviders = new LinkedList<>();
    final List<WoverDataProvider<?>> providerFactories = new LinkedList<>();
    DatapackBootstrap datapackBootstrap;

    PackBuilder(@Nullable ModCore modCore, @NotNull ResourceLocation location) {
        this.location = location;
        this.modCore = modCore;
    }

    /**
     * Adds a {@link WoverRegistryProvider} to the Datapack.
     *
     * @param provider The {@link WoverRegistryProvider} to add
     * @param <T>      The element type of the registry.
     * @return This instance
     */
    public <T> PackBuilder addRegistryProvider(RegistryFactory<T> provider) {
        registryProviders.add(provider.create(modCore));
        return this;
    }

    public <T extends DataProvider> PackBuilder addProvider(ProviderFactory<T> provider) {
        providerFactories.add(provider.create(modCore));
        return this;
    }

    /**
     * Sets the {@link DatapackBootstrap} for this Datapack. The linked
     * method will be called when the Datapack was created. You can use this
     * to add custom providers to the Datapack.
     *
     * @param datapackBootstrap The {@link DatapackBootstrap} to use or {@code null}
     *                          to remove the current one.
     * @return This instance
     */
    public PackBuilder callOnInitializeDatapack(@Nullable DatapackBootstrap datapackBootstrap) {
        this.datapackBootstrap = datapackBootstrap;
        return this;
    }


    PackBuilder pack(FabricDataGenerator.Pack pack) {
        this.pack = pack;
        return this;
    }

    /**
     * A functional interface that is used to create a {@link WoverRegistryProvider}.
     *
     * @param <T> The element type of the registry.
     */
    @FunctionalInterface
    public interface RegistryFactory<T> {
        /**
         * Creates a new {@link WoverRegistryProvider}.
         *
         * @param modCore The ModCore instance of the Mod that is providing this instance.
         * @return The created {@link WoverRegistryProvider}.
         */
        WoverRegistryProvider<T> create(ModCore modCore);
    }

    /**
     * A functional interface that is used to bootstrap content for a Datapack.
     */
    @FunctionalInterface
    public interface DatapackBootstrap {
        /**
         * Called when the Datapack is created.
         *
         * @param fabricDataGenerator The Fabric Datagenerator
         * @param pack                The Datapack
         * @param location            The {@link ResourceLocation} of the Datapack
         */
        void bootstrap(
                FabricDataGenerator fabricDataGenerator,
                FabricDataGenerator.Pack pack,
                ResourceLocation location
        );
    }

    @FunctionalInterface
    public interface ProviderFactory<T extends DataProvider> {
        WoverDataProvider<T> create(ModCore modCore);
    }
}
