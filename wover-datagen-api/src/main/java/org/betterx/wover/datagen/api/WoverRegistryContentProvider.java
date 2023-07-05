package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class WoverRegistryContentProvider<T> extends WoverRegistryProvider<T> {
    private final List<ResourceKey<T>> content;

    public WoverRegistryContentProvider(
            ModCore modCore,
            String title,
            ResourceKey<Registry<T>> registryKey
    ) {
        super(modCore, title, registryKey);
        this.content = new LinkedList<>();
    }

    protected abstract void bootstrap(BootstapContext<T> context);

    protected void addContent(ResourceKey<T> resourceKey) {
        content.add(resourceKey);
    }

    private void wrappedBoostrap(BootstapContext<T> context) {
        BootstapContext<T> wrapped = new BootstapContext<T>() {
            @Override
            public Holder.Reference<T> register(ResourceKey<T> resourceKey, T object, Lifecycle lifecycle) {
                addContent(resourceKey);
                return context.register(resourceKey, object, lifecycle);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return context.lookup(resourceKey);
            }
        };
        bootstrap(wrapped);
    }

    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        modCore.log.info("Registering " + title);
        registryBuilder.add(registryKey, this::wrappedBoostrap);
    }

    protected FabricDynamicRegistryProvider getProvider(
            FabricDataOutput output,
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
                modCore.log.info("[" + count + " / " + content.size() + "] " + registryKey.location());
            }

            @Override
            public String getName() {
                return title;
            }
        };
    }
}
