package org.betterx.wover.generator.impl.biomesource;

import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.generator.api.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.api.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

import org.jetbrains.annotations.ApiStatus;

public class BiomeSourceManagerImpl {
    public static void register(ResourceLocation location, Codec<? extends BiomeSource> codec) {
        Registry.register(BuiltInRegistries.BIOME_SOURCE, location, codec);
    }

    @ApiStatus.Internal
    public static void initialize() {
        register(WoverWorldGenerator.C.id("nether_biome_source"), WoverNetherBiomeSource.CODEC);
        register(LegacyHelper.BCLIB_CORE.id("nether_biome_source"), LegacyHelper.wrap(WoverNetherBiomeSource.CODEC));

        register(WoverWorldGenerator.C.id("end_biome_source"), WoverEndBiomeSource.CODEC);
        register(LegacyHelper.BCLIB_CORE.id("end_biome_source"), LegacyHelper.wrap(WoverEndBiomeSource.CODEC));
    }
}
