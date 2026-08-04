package de.ambertation.wover.datagen.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.entrypoint.LibWoverDatagen;

import net.minecraft.data.DataProvider;

import java.util.LinkedList;
import java.util.List;

public abstract class PackBuilderImpl {
    protected final List<WoverDataProvider<?>> providerFactories = new LinkedList<>();
    protected final List<WoverAutoProvider.WithRedirect> redirectors = new LinkedList<>();

    protected abstract ModCore modCore();

    /**
     * Adds a to the Datapack. The method will check all availabe redirectors and apply them to the provider.
     * If any redirector returns null, the provider will not be added.
     *
     * @param provider The {@link WoverDataProvider<T>} to add
     * @param <T>      The element type of the registry.
     */
    protected <T extends DataProvider> void addProviderWithRedirect(WoverDataProvider<T> provider) {
        final WoverDataProvider<T> p = redirectors
                .stream()
                .reduce(provider, (prov, redirector) -> redirector.redirect(prov), (a, b) -> a);
        if (p != null) {
            providerFactories.add(p);
        }
    }

    /**
     * Adds an AutoProvider to the Datapack. If the provider is an instance of {@link WoverAutoProvider.WithRedirect},
     * it will be added to the redirectors list, otherwise it will be added to the providerFactories list and available
     * redirectors will be applied to it.
     * <p>
     * The method assuemes that autoProviders are instantiated in an order where providers with redirectors are
     * instantiated first.
     *
     * @param provider The {@link WoverDataProvider<T>} to add
     * @param <T>      The element type of the registry.
     */
    <T extends DataProvider> void instantiateAutoProvider(
            WoverDataProvider<T> provider
    ) {
        if (provider == null) return;
        LibWoverDatagen.C.LOG.debug("Instantiating auto provider: {}", provider.getClass().getName());
        if (provider instanceof WoverAutoProvider.WithRedirect) {
            redirectors.add((WoverAutoProvider.WithRedirect) provider);
            providerFactories.add(provider);
        } else {
            addProviderWithRedirect(provider);
        }
    }
}
