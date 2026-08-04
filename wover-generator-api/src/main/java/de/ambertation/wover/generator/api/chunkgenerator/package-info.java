/**
 * Registers custom {@link net.minecraft.world.level.chunk.ChunkGenerator} types.
 * <p>
 * See {@link de.ambertation.wover.generator.api.chunkgenerator.ChunkGeneratorManager} for the main entry
 * point. WoVer's own generator implementation
 * ({@link de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator}) lives in the {@code impl}
 * package instead, since most mod developers only need to configure it through a
 * {@link de.ambertation.wover.generator.api.biomesource.WoverBiomeSource} rather than implement a generator
 * themselves.
 */
package de.ambertation.wover.generator.api.chunkgenerator;
