package de.ambertation.wover.generator.mixin.biomesource;

import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.core.api.registry.DatapackRegistryLoadOrder;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;

import net.minecraft.core.HolderGetter;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes {@code minecraft:nether} see every modded Nether biome, on every load.
 * <p>
 * The constructor bakes {@code preset.provider.apply(...)} into an immutable
 * {@link net.minecraft.world.level.biome.Climate.ParameterList} right there and then, and Fabric's
 * {@code NetherBiomePresetMixin} appends {@code NetherBiomeData.NETHER_BIOME_NOISE_POINTS} to that
 * result. So whatever is in Fabric's table at this exact instant is what the preset will contain
 * for the rest of the load - there is no later chance to add to it.
 * <p>
 * {@link de.ambertation.wover.generator.impl.biomesource.BiomeSourceManagerImpl} fills that table
 * from the {@code wover:wover/worldgen/biome_data} registry's element callback, and since 26.1.2
 * registry load tasks all run at once, the two are unordered. Measured on a dedicated server with
 * BetterNether's 23 Nether biomes, the preset that ended up in the world's registry held 5 entries
 * (no modded biomes at all) on most boots, and 15 or 21 or the full 28 on the rest - same jar, same
 * seed, different every boot.
 * <p>
 * Waiting for {@code biome_data} here turns that into an ordering: our writes are guaranteed to be
 * finished before the bake happens. The dependency is acyclic - decoding {@code biome_data} never
 * touches the parameter list registry - and it costs nothing outside datapack loading, where
 * {@code awaitElements} returns immediately.
 * <p>
 * Why this stayed unnoticed: nothing WoVer manages reads this preset. Our own Nether generator is
 * installed over the vanilla one even for a {@code minecraft:normal} world, and it picks Biomes by
 * {@link BiomeTags#IS_NETHER} - which {@code BiomeRepairHelper.registerAllBiomesFromFabric}
 * populates from Fabric's <em>live</em> table, not from this snapshot. The snapshot only reaches
 * things we do not control: a datapack dimension declaring {@code "preset": "minecraft:nether"}, or
 * another mod resolving the parameter list out of the world's registries.
 * <p>
 * Historical note, in case an old log turns up: {@code wover-event-api}'s {@code WorldLoaderMixin}
 * used to run a second, throwaway {@code RegistryDataLoader.load(WORLDGEN_REGISTRIES)} from the
 * <em>dimension</em> stage, so a boot log showed this preset baked twice. Only the first bake was
 * ever the world's; the second always looked complete because Fabric's table is a never-cleared
 * static. That extra load is gone - one bake per world load now.
 * <p>
 * The End side of the same problem is already ordered: Fabric memoizes
 * {@code TheEndBiomeData.Overrides} behind a {@code Supplier} that nothing forces until well after
 * loading. See {@link NetherBiomeDataMixin} for the crash the same race used to cause, and
 * {@code NetherPresetGameTest} for the regression guard.
 */
@Mixin(MultiNoiseBiomeSourceParameterList.class)
public class MultiNoiseBiomeSourceParameterListMixin {
    // Static because an @Inject at the HEAD of a constructor runs before super(), where there is no
    // `this` yet. We do not need one.
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void wover_awaitBiomeData(
            MultiNoiseBiomeSourceParameterList.Preset preset,
            HolderGetter<Biome> biomes,
            CallbackInfo ci
    ) {
        DatapackRegistryLoadOrder.awaitElements(BiomeDataRegistry.BIOME_DATA_REGISTRY);
    }

    // Records what each bake actually produced. A world load bakes every preset at least once per
    // RegistryDataLoader.load, and with the ordering above every bake of the same preset has to come
    // out the same size - that is the invariant this whole mixin exists to hold, and comparing these
    // lines is the cheapest way to see it broken again.
    @Inject(method = "<init>", at = @At("RETURN"))
    private void wover_logBakedSize(
            MultiNoiseBiomeSourceParameterList.Preset preset,
            HolderGetter<Biome> biomes,
            CallbackInfo ci
    ) {
        LibWoverWorldGenerator.C.log.verbose(
                "Baked biome parameter list " + preset.id()
                        + ": " + ((MultiNoiseBiomeSourceParameterList) (Object) this).parameters().values().size()
                        + " entries"
        );
    }
}
