/**
 * Custom {@link net.minecraft.world.level.biome.BiomeSource}s that place Biomes based on the Biome tags
 * ({@link net.minecraft.tags.TagKey}) they carry, and the extra placement data
 * ({@link de.ambertation.wover.generator.api.biomesource.WoverBiomeData}) that drives sub-biome/edge-biome
 * placement.
 * <p>
 * See {@link de.ambertation.wover.generator.api.biomesource.WoverBiomeSource} for the shared base class,
 * {@link de.ambertation.wover.generator.api.biomesource.WoverBiomeBuilder} for the fluent builder used to
 * declare WoVer-aware Biomes from a
 * {@link de.ambertation.wover.biome.api.builder.BiomeBootstrapContext}, and
 * {@link de.ambertation.wover.generator.api.biomesource.BiomeSourceManager} to register new
 * {@link net.minecraft.world.level.biome.BiomeSource} types. The {@code end}/{@code nether} subpackages hold
 * the configuration classes of WoVer's own End/Nether biome sources.
 */
package de.ambertation.wover.generator.api.biomesource;
