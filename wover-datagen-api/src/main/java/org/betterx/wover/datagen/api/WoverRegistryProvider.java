package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class WoverRegistryProvider<T> {
    public final String title;
    public final Predicate<String> validNamespace;

    public final ResourceKey<Registry<T>> registryKey;
    final ModCore modCore;

    public WoverRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey,
            Predicate<String> validNamespace
    ) {
        this.modCore = modCore;
        this.title = title;
        this.validNamespace = validNamespace;
        this.registryKey = registryKey;
    }

    public WoverRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey,
            List<String> validNamespaces
    ) {
        this(modCore, title, registryKey, validNamespaces::contains);
    }

    public WoverRegistryProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        this(modCore, title, registryKey, List.of(modCore.namespace));
    }

    protected abstract void bootstrap(BootstapContext<T> context);

    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        modCore.log.info("Registering " + title);
        registryBuilder.add(registryKey, this::bootstrap);
    }

    protected FabricDynamicRegistryProvider getProvider(
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
