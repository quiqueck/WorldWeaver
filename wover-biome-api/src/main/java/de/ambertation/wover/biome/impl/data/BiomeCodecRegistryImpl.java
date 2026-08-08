package de.ambertation.wover.biome.impl.data;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.core.api.registry.BuiltInRegistryManager;
import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverBiome;
import de.ambertation.wover.entrypoint.LibWoverSurface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class BiomeCodecRegistryImpl {
    // Owned here rather than on the public BiomeCodecRegistry for the same class-initialization-cycle
    // reason described in BiomePredicateRegistryImpl: LibWoverBiome touches this class first, so
    // BiomeCodecRegistry.BIOME_CODECS was left null. Note the registry id keeps LibWoverSurface's namespace
    // it was originally created with, so no datapack path changes.
    public static final ResourceKey<Registry<MapCodec<? extends BiomeData>>> BIOME_CODEC_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverSurface.C.id("wover/biome_codec"));

    public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BuiltInRegistryManager.createRegistry(
            BIOME_CODEC_REGISTRY,
            BiomeCodecRegistryImpl::onBootstrap
    );

    public static final Codec<BiomeData> CODEC = BIOME_CODECS
            .byNameCodec()
            .dispatch(BiomeData::codec, Function.identity());

    public static MapCodec<? extends BiomeData> register(
            Registry<MapCodec<? extends BiomeData>> registry,
            Identifier location,
            MapCodec<? extends BiomeData> codec
    ) {
        return BuiltInRegistryManager.register(registry, location, codec);
    }

    @ApiStatus.Internal
    public static void initialize() {
        onBootstrap(BIOME_CODECS);
    }

    private static MapCodec<? extends BiomeData> onBootstrap(Registry<MapCodec<? extends BiomeData>> registry) {
        final var biomeData = LibWoverBiome.C.id("vanilla_data");
        if (registry.containsKey(biomeData)) {
            return registry.get(biomeData).orElseThrow().value();
        }

        return register(registry, biomeData, BiomeData.CODEC);
    }
}
