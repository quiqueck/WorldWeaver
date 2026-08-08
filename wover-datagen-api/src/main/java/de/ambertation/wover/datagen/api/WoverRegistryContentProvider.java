package de.ambertation.wover.datagen.api;

import de.ambertation.wover.core.api.ModCore;

import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;


/**
 * Handles the boostrapping as well as the serialization of a {@link Registry} to
 * a DataPack. This is a special version of {@link WoverRegistryProvider} that
 * will only serilaize the elements that were registered in the #bootstrap method.
 *
 * @param <T> The element type of the registry.
 */
public abstract class WoverRegistryContentProvider<T> extends WoverRegistryProvider<T> {
    private final List<ResourceKey<T>> content;

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore     The ModCore instance of the Mod that is providing this instance.
     * @param title       The title of the provider. Mainly used for logging.
     * @param registryKey The Key to the Registry.
     */
    public WoverRegistryContentProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        super(modCore, title, registryKey);
        this.content = new LinkedList<>();
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * Only Elements that are registered in this method
     * (using {@link BootstrapContext#register(ResourceKey, Object)}) will be serialized.
     * <p>
     * Minecraft 26.3 dropped the {@code register(ResourceKey, Object, Lifecycle)} overload from
     * {@link BootstrapContext}, so the two-argument form is the only registration entry point left.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(BootstrapContext<T> context);

    private void addContent(ResourceKey<T> resourceKey) {
        content.add(resourceKey);
    }

    private void wrappedBoostrap(BootstrapContext<T> context) {
        BootstrapContext<T> wrapped = new BootstrapContext<T>() {
            // Minecraft 26.3 removed BootstrapContext#register(ResourceKey, T, Lifecycle); only the
            // two-argument form remains, and every registration now uses the registry's own lifecycle.
            // We used to only wrap the lifecycle-aware overload, which worked because the plain
            // register(ResourceKey, T) of the wrapped context was never reachable through this
            // decorator anyway. Recording the key in the surviving overload keeps the tracking
            // complete - nothing is lost, the lifecycle argument simply has no counterpart in 26.3.
            @Override
            public Holder.Reference<T> register(ResourceKey<T> resourceKey, T object) {
                addContent(resourceKey);
                return context.register(resourceKey, object);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return context.lookup(resourceKey);
            }

            // New abstract method in 26.3, declared on the extracted BootstrapContextAccess
            // super-interface of BootstrapContext. This class is a pure decorator that only
            // observes registrations, so it forwards the query to the wrapped context verbatim.
            // Vanilla marks the method @Deprecated (it exposes the partially-built surrounding
            // context and is meant for bootstrap code only), but it is abstract, so an anonymous
            // BootstrapContext has no choice but to implement it. Same situation as in
            // DatapackRegistryBuilder#makeContext.
            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(
                    ResourceKey<? extends Registry<? extends S>> resourceKey
            ) {
                return context.listContextElements(resourceKey);
            }
        };
        bootstrap(wrapped);
    }

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
        registryBuilder.add(registryKey, this::wrappedBoostrap);
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
    public final FabricDynamicRegistryProvider getProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                final var registry = registries.lookupOrThrow(registryKey);
                int count = 0;
                for (var key : content) {
                    var optional = registry.get(key);
                    if (optional.isPresent() && optional.get().isBound()) {
                        entries.add(key, optional.get().value());
                        count++;
                    }
                }
                modCore.log.info("[" + count + " / " + content.size() + "] " + registryKey.identifier());
            }

            @Override
            public String getName() {
                return title;
            }
        };
    }
}
