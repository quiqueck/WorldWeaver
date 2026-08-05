package de.ambertation.wover.datagen.api;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.impl.PackBuilderImpl;

import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A builder for a Datapack. This class is used to manage the content of the Datapack.
 * <p>
 * You can create new Instances of this class using {@link WoverDataGenEntryPoint#addDatapack(Identifier)}
 */
public class PackBuilder extends PackBuilderImpl {
    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    @NotNull
    public final ModCore modCore;

    /**
     * The {@link Identifier} of the Datapack or {@code null} if it is the global
     * Datapack.
     */
    @Nullable
    public final Identifier location;

    FabricDataGenerator.Pack pack;
    DatapackBootstrap datapackBootstrap;


    PackBuilder(@NotNull ModCore modCore, @Nullable Identifier location) {
        super();
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
        providerFactories.add(provider.create(modCore));
        return this;
    }

    /**
     * Adds a {@link ProviderFactory} to the Datapack.
     *
     * @param provider The {@link ProviderFactory} to add
     * @param <T>      The element type of the registry.
     * @return This instance
     */
    public <T extends DataProvider> PackBuilder addProvider(ProviderFactory<T> provider) {
        super.addProviderWithRedirect(provider.create(modCore));
        return this;
    }


    /**
     * Adds a {@link WoverMultiProvider} to the Datapack.
     *
     * @param provider The {@link WoverMultiProvider} to add
     * @param <T>      The element type of the registry.
     * @return This instance
     */
    public <T extends WoverMultiProvider> PackBuilder addMultiProvider(MultiProviderFactory<T> provider) {
        provider.create(modCore).registerAllProviders(this);
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

    List<WoverDataProvider<?>> providerFactories() {
        return this.providerFactories;
    }

    /**
     * Returns a {@link Stream} of all {@link WoverRegistryProvider}s that are
     * registered for this Datapack.
     *
     * @return The {@link Stream} of {@link WoverRegistryProvider}s
     */
    public List<? extends WoverRegistryProvider<?>> registryProviders() {
        return this.providerFactories
                .stream()
                .filter(provider -> provider instanceof WoverRegistryProvider<?>)
                .map(provider -> (WoverRegistryProvider<?>) provider)
                .toList();
    }


    PackBuilder pack(FabricDataGenerator.Pack pack) {
        this.pack = pack;
        return this;
    }

    @Override
    protected ModCore modCore() {
        return modCore;
    }

    /**
     * A functional interface that is used to create a {@link WoverRegistryProvider}.
     *
     * @param <T> The element type of the registry.
     */
    @FunctionalInterface
    public interface RegistryFactory<T> extends ProviderFactory<FabricDynamicRegistryProvider> {
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
         * @param location            The {@link Identifier} of the Datapack
         */
        void bootstrap(
                FabricDataGenerator fabricDataGenerator,
                FabricDataGenerator.Pack pack,
                Identifier location
        );
    }

    /**
     * A functional interface that is used to create a {@link WoverDataProvider}.
     *
     * @param <T> The Provider Type.
     */
    @FunctionalInterface
    public interface ProviderFactory<T extends DataProvider> {
        /**
         * Creates a new {@link WoverDataProvider}.
         *
         * @param modCore The ModCore instance of the Mod that is providing this instance.
         * @return The created {@link WoverDataProvider}.
         */
        WoverDataProvider<T> create(ModCore modCore);
    }

    /**
     * A functional interface that is used to create a {@link WoverMultiProvider}.
     *
     * @param <T> The Provider Type.
     */
    @FunctionalInterface
    public interface MultiProviderFactory<T extends WoverMultiProvider> {
        /**
         * Creates a new {@link WoverMultiProvider}.
         *
         * @param modCore The ModCore instance of the Mod that is providing this instance.
         * @return The created {@link WoverMultiProvider}.
         */
        T create(ModCore modCore);
    }
}
