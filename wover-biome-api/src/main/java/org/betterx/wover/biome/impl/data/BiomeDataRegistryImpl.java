package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

public class BiomeDataRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<BiomeData>> BOOTSTRAP_BIOME_DATA_REGISTRY
            = new EventImpl<>("BOOTSTRAP_BIOME_DATA_REGISTRY");

    private static void onBootstrap(BootstapContext<BiomeData> ctx) {
        BOOTSTRAP_BIOME_DATA_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.register(
                BiomeDataRegistry.BIOME_DATA_REGISTRY,
                BiomeCodecRegistryImpl.CODEC,
                BiomeDataRegistryImpl::onBootstrap
        );
    }

    public static ResourceKey<BiomeData> createKey(
            ResourceLocation ruleID
    ) {
        return ResourceKey.create(
                BiomeDataRegistry.BIOME_DATA_REGISTRY,
                ruleID
        );
    }

}
