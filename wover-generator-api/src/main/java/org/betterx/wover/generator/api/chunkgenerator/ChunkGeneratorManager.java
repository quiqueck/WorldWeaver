package org.betterx.wover.generator.api.chunkgenerator;

import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;

/**
 * Registers custom {@link ChunkGenerator} types.
 * <p>
 * Registering a new {@link ChunkGenerator} type here is only necessary if you are implementing your own
 * generator (comparable to WoVer's own {@code wover:betterx} generator). Most mod developers instead reuse
 * that generator with a custom {@link org.betterx.wover.generator.api.biomesource.WoverBiomeSource
 * WoverBiomeSource}.
 */
public class ChunkGeneratorManager {
    /**
     * The event priority WoVer's own {@code CREATED_NEW_WORLD_FOLDER} subscriber uses to write the initial
     * per-dimension world-generator config when a new world is created. Register your own subscriber with a
     * lower priority if it needs to run after WoVer's config was written.
     */
    public static final int CREATE_DIMENSION_CONFIG_PRIORITY = 20000;

    /**
     * Registers a new {@link ChunkGenerator} type under the given id.
     * <p>
     * This is the same mechanism vanilla uses to register {@link ChunkGenerator} codecs
     * ({@code minecraft:chunk_generator}, e.g. {@code noise} or {@code flat}) — it makes {@code type: <location>}
     * a valid value for the {@code chunk_generator} field in {@code data/<namespace>/dimension/*.json}.
     *
     * @param location The id the {@link ChunkGenerator} type is registered under.
     * @param codec    The {@link MapCodec} used to (de)serialize the {@link ChunkGenerator}.
     */
    public static void register(ResourceLocation location, MapCodec<ChunkGenerator> codec) {
        ChunkGeneratorManagerImpl.register(location, codec);
    }
}
