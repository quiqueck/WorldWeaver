package de.ambertation.wover.generator.mixin.biomesource;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps Fabric's Nether biome list usable while datapack registries load in parallel.
 * <p>
 * {@code fabric-biome-api-v1} stores modded Nether biomes in a plain {@link java.util.HashMap} and
 * neither the writer ({@code addNetherBiome}) nor the readers ({@code withModdedBiomeEntries}, which
 * backs both {@code MultiNoiseBiomeSourceParameterList.Preset.NETHER} and
 * {@code NetherBiomes.canGenerateInNether}) synchronize. That was safe while the API was only ever
 * touched from mod init, but it no longer is:
 * <ul>
 *     <li>{@code RegistryDataLoader} starts every registry's load task at once and joins them with
 *     {@code CompletableFuture.allOf}, so the tasks run concurrently.</li>
 *     <li>{@link de.ambertation.wover.generator.impl.biomesource.BiomeSourceManagerImpl} registers
 *     Nether biomes with Fabric from the {@code biome_data} registry's element callback, i.e. on
 *     that registry's load task.</li>
 *     <li>Meanwhile the {@code multi_noise_biome_source_parameter_list} task decodes
 *     {@code minecraft:nether} on a ForkJoin worker, which iterates the very same map.</li>
 * </ul>
 * The two collide often enough to fail roughly one dedicated-server boot in four:
 * <pre>
 * IllegalStateException: Unbound values in registry
 *     [minecraft:worldgen/multi_noise_biome_source_parameter_list]: [minecraft:nether]
 * Caused by: java.util.ConcurrentModificationException
 *     at java.util.HashMap$HashIterator.nextNode(HashMap.java:1606)
 *     at net.fabricmc.fabric.impl.biome.NetherBiomeData.withModdedBiomeEntries(NetherBiomeData.java:70)
 * </pre>
 * followed by {@code Failed to load datapacks, can't proceed with server load} and an exit code of
 * 0, which reads like a clean shutdown rather than a crash.
 * <p>
 * Swapping the backing map for a {@link ConcurrentHashMap} makes the readers weakly consistent
 * instead of fail-fast, so they can no longer throw. Behaviour is otherwise identical: the map is
 * keyed by {@link ResourceKey}, which is interned and does not override
 * {@code equals}/{@code hashCode}, and the writes all come from a single thread (a registry's
 * {@code registerElements} step is sequential), so no entry can be lost.
 * <p>
 * Note that this fixes the crash, not the underlying ordering: whether {@code minecraft:nether}
 * sees our biomes still depends on which registry task wins the race. See
 * {@link TheEndBiomeDataMixin} for the same problem on the End side.
 * <p>
 * Reported upstream against {@code fabric-biome-api-v1} 18.0.6 (bundled with fabric-api
 * 0.156.0+26.2); the map is still unsynchronized on Fabric's default branch. Once Fabric makes the
 * collection thread-safe itself this becomes a redundant copy with identical behaviour, so it is
 * safe to keep either way.
 */
@Mixin(targets = "net.fabricmc.fabric.impl.biome.NetherBiomeData", remap = false)
public class NetherBiomeDataMixin {
    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<Biome>, Climate.ParameterPoint> NETHER_BIOME_NOISE_POINTS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_makeNoisePointsConcurrent(CallbackInfo ci) {
        NETHER_BIOME_NOISE_POINTS = new ConcurrentHashMap<>(NETHER_BIOME_NOISE_POINTS);
    }
}
