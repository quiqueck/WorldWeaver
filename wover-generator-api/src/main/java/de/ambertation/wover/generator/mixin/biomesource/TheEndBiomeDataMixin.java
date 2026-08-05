package de.ambertation.wover.generator.mixin.biomesource;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The End counterpart to {@link NetherBiomeDataMixin}, closing the same race on the same code path.
 * <p>
 * {@code TheEndBiomeData} keeps its replacement tables in three {@link java.util.IdentityHashMap}s
 * plus a {@link java.util.HashSet}, all unsynchronized.
 * {@link de.ambertation.wover.generator.impl.biomesource.BiomeSourceManagerImpl} writes to them from
 * the {@code biome_data} registry's element callback, while {@code TheEndBiomeSource} reaches
 * {@code possibleBiomes() -> modifyBiomeSet() -> createOverrides()} on a different registry's load
 * task; {@code Overrides}'s constructor iterates every one of these collections. Because registry
 * load tasks run concurrently, that iteration can hit a
 * {@link java.util.ConcurrentModificationException} exactly like the Nether one does.
 * <p>
 * The End side is easier to get away with in practice - the {@code Overrides} are memoized behind a
 * {@code Supplier}, so how much of the load overlaps depends on when the End biome source is first
 * asked for its biomes - but the failure mode is the same, and so is the fix.
 * <p>
 * {@link ConcurrentHashMap} is a faithful replacement for {@code IdentityHashMap} here: every key is
 * a {@link ResourceKey}, which is interned through a {@code ConcurrentMap} in
 * {@code ResourceKey.create} and does not override {@code equals}/{@code hashCode}, so equality
 * already <em>is</em> identity.
 */
@Mixin(targets = "net.fabricmc.fabric.impl.biome.TheEndBiomeData", remap = false)
public class TheEndBiomeDataMixin {
    // Raw-ish value types: the real value is fabric's package-private WeightedPicker, and mixin only
    // matches on the erased descriptor, so Object keeps this compilable without depending on it.
    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<Biome>, Object> END_BIOMES_MAP;

    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<Biome>, Object> END_MIDLANDS_MAP;

    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<Biome>, Object> END_BARRENS_MAP;

    @Shadow
    @Final
    @Mutable
    public static Set<ResourceKey<Biome>> ADDED_BIOMES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_makeEndTablesConcurrent(CallbackInfo ci) {
        // TAIL, so the vanilla defaults the static initializer just put in are carried over.
        END_BIOMES_MAP = new ConcurrentHashMap<>(END_BIOMES_MAP);
        END_MIDLANDS_MAP = new ConcurrentHashMap<>(END_MIDLANDS_MAP);
        END_BARRENS_MAP = new ConcurrentHashMap<>(END_BARRENS_MAP);

        final Set<ResourceKey<Biome>> added = Collections.newSetFromMap(new ConcurrentHashMap<>());
        added.addAll(ADDED_BIOMES);
        ADDED_BIOMES = added;
    }
}
