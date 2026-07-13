/**
 * Registers custom {@link net.minecraft.world.level.chunk.ChunkGenerator} types.
 * <p>
 * See {@link org.betterx.wover.generator.api.chunkgenerator.ChunkGeneratorManager} for the main entry
 * point. WoVer's own generator implementation
 * ({@link org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGenerator}) lives in the {@code impl}
 * package instead, since most mod developers only need to configure it through a
 * {@link org.betterx.wover.generator.api.biomesource.WoverBiomeSource} rather than implement a generator
 * themselves.
 */
package org.betterx.wover.generator.api.chunkgenerator;
