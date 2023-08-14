package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;

/**
 * Handles the bootstrapping as well as the serialization of a {@link Registry} to
 * a DataPack. This version of the Registry Provider will serialize all elements
 * of the Registry, that have a valid namespace.
 *
 * @param <T> The element type of the registry.
 */
public abstract class WoverFullRegistryProvider<T> extends WoverRegistryProvider<T> {
    /**
     * The predicate to check if a namespace is valid.
     */
    public final Predicate<String> validNamespace;


    /**
     * Creates a new instance of {@link WoverFullRegistryProvider} with a {@link Predicate} to
     * determine valid namespaces
     *
     * @param modCore        The ModCore instance of the Mod that is providing this instance.
     * @param title          The title of the provider. Mainly used for logging.
     * @param registryKey    The Key to the Registry.
     * @param validNamespace The predicate to check if a namespace is valid. All elements
     *                       from the Registry with a valid namespace will be serialized.
     */
    public WoverFullRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey,
            Predicate<String> validNamespace
    ) {
        super(modCore, title, registryKey);
        this.validNamespace = validNamespace;
    }

    /**
     * Creates a new instance of {@link WoverFullRegistryProvider} with a list of valid namespaces.
     *
     * @param modCore         The ModCore instance of the Mod that is providing this instance.
     * @param title           The title of the provider. Mainly used for logging.
     * @param registryKey     The Key to the Registry.
     * @param validNamespaces A list of valid namespaces. All elements from the registry whose
     *                        namespace is contained in the list will be serialized.
     */
    public WoverFullRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey,
            List<String> validNamespaces
    ) {
        this(modCore, title, registryKey, validNamespaces::contains);
    }

    /**
     * Creates a new instance of {@link WoverFullRegistryProvider} that will only
     * allow elements with the namespace of the Mod (as determined by {@link ModCore#namespace}).
     *
     * @param modCore     The ModCore instance of the Mod that is providing this instance.
     * @param title       The title of the provider. Mainly used for logging.
     * @param registryKey The Key to the Registry.
     */
    public WoverFullRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        this(modCore, title, registryKey, List.of(modCore.namespace));
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * Which elements will be serialized is determined by the {@link #validNamespace}
     * {@link Predicate}.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(BootstapContext<T> context);

    /**
     * Adds the Registry to the given {@link RegistrySetBuilder}. This method is
     * called internally by {@link WoverDataGenEntryPoint#buildRegistry(RegistrySetBuilder)}
     *
     * @param registryBuilder The builder to add the registry to.
     */
    @ApiStatus.Internal
    @Override
    public final void buildRegistry(RegistrySetBuilder registryBuilder) {
        modCore.log.info("Registering " + title);
        registryBuilder.add(registryKey, this::bootstrap);
    }

    /**
     * Gets the {@link FabricDynamicRegistryProvider} that will serialize the
     * Registry to the DataPack. This method is called internally by
     * {@link WoverDataGenEntryPoint#onInitializeDataGenerator(FabricDataGenerator)}
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return The {@link FabricDynamicRegistryProvider} that will serialize the
     * Registry to the DataPack.
     */
    @ApiStatus.Internal
    @Override
    public FabricDynamicRegistryProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                final var registry = registries.lookupOrThrow(registryKey);
                final long countAll = registry.listElementIds().count();
                var filtered = registry.listElementIds()
                                       .filter(key -> validNamespace.test(key.location().getNamespace()))
                                       .map(registry::getOrThrow)
                                       .filter(Holder.Reference::isBound)
                                       .toList();
                final long filteredCount = filtered.size();
                filtered.forEach(holder -> entries.add(holder.key(), holder.value()));
                modCore.log.info("[" + filteredCount + " / " + countAll + "] " + registryKey.location());
            }

            @Override
            public String getName() {
                return title;
            }
        };
    }
}
