package org.betterx.wover.generator.api.chunkgenerator;

import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ChunkGeneratorManager {
    public static void register(ResourceLocation location, Codec<ChunkGenerator> codec) {
        ChunkGeneratorManagerImpl.register(location, codec);
    }
}
