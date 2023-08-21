package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

public class BiomeSourceManager {
    public void register(ResourceLocation location, Codec<BiomeSource> codec) {
        BiomeSourceManagerImpl.register(location, codec);
    }
}
