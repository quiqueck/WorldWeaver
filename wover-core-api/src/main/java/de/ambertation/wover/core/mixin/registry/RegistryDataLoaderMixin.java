package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;
import de.ambertation.wover.entrypoint.LibWoverCore;

import com.mojang.serialization.Decoder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Accessor("WORLDGEN_REGISTRIES")
    @Mutable
    static void wt_set_WORLDGEN_REGISTRIES(List<RegistryDataLoader.RegistryData<?>> list) {
        //SHADOWED
    }

    /**
     * Inserts our Datapack-backed registries into {@code WORLDGEN_REGISTRIES}, in front of
     * {@link Registries#MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST}.
     * <p>
     * Position matters here in a way it does not on 26.1+. In 1.21.x {@code RegistryDataLoader} loads the
     * list strictly in order, and {@code minecraft:nether}'s parameter list is baked from Fabric's
     * {@code NetherBiomeData} table the moment it is decoded. We fill that table from the
     * {@code wover:biome_data} registry's element callback, so appending our registries - as this used to
     * do - meant {@code biome_data} always ran <i>after</i> the bake and the preset came out with the five
     * vanilla Nether Biomes and nothing else, on every single boot. Measured before this change on MC
     * 1.21.8 with BetterNether installed: 5 entries, then 27 in the throwaway second load that
     * {@code WorldLoaderMixin} performs (never the world's).
     * <p>
     * Inserting rather than prepending keeps the change as small as possible: every vanilla registry that
     * used to load before ours still does, except the handful that follow the parameter list (banner
     * patterns, enchantments, jukebox songs, ...), none of which our bootstraps touch. If the anchor ever
     * disappears we fall back to appending, i.e. to the old behaviour.
     * <p>
     * 26.1+ solves the same problem differently, because there the registries load concurrently and no list
     * order can express it - see {@code DatapackRegistryLoadOrder} on those branches.
     */
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_init(CallbackInfo ci) {
        final List<RegistryDataLoader.RegistryData<?>> custom = new ArrayList<>();
        LibWoverCore.C.log.debug("Adding custom WORLDGEN_REGISTRIES");
        DatapackRegistryBuilderImpl.forEach((key, codec) -> {
            if (codec != null) {
                LibWoverCore.C.log.debug("    - Adding " + key.location());
                custom.add(new RegistryDataLoader.RegistryData(key, codec, false));
            }
        });

        int insertAt = RegistryDataLoader.WORLDGEN_REGISTRIES.size();
        for (int i = 0; i < RegistryDataLoader.WORLDGEN_REGISTRIES.size(); i++) {
            if (RegistryDataLoader.WORLDGEN_REGISTRIES
                    .get(i)
                    .key()
                    .equals(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)) {
                insertAt = i;
                break;
            }
        }
        if (insertAt == RegistryDataLoader.WORLDGEN_REGISTRIES.size()) {
            LibWoverCore.C.log.warn(
                    "Did not find " + Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST.location()
                            + " in WORLDGEN_REGISTRIES; appending our registries instead. Modded Nether "
                            + "Biomes may be missing from minecraft:nether."
            );
        }

        final List<RegistryDataLoader.RegistryData<?>> enhanced =
                new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES.size() + custom.size());
        enhanced.addAll(RegistryDataLoader.WORLDGEN_REGISTRIES.subList(0, insertAt));
        enhanced.addAll(custom);
        enhanced.addAll(RegistryDataLoader.WORLDGEN_REGISTRIES.subList(
                insertAt,
                RegistryDataLoader.WORLDGEN_REGISTRIES.size()
        ));

        wt_set_WORLDGEN_REGISTRIES(enhanced);
    }

    @Inject(
            method = "loadContentsFromManager",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/tags/TagLoader;loadTagsForRegistry(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/WritableRegistry;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private static <E> void wover_bootstrap(
            ResourceManager resourceManager,
            RegistryOps.RegistryInfoLookup registryInfoLookup,
            WritableRegistry<E> writableRegistry,
            Decoder<E> decoder,
            Map<ResourceKey<?>, Exception> map,
            CallbackInfo ci
    ) {
        DatapackRegistryBuilderImpl.bootstrap(registryInfoLookup, writableRegistry.key(), writableRegistry);
    }

    //we moved this over to the register Method in MappedRegistryMixin to catch all registered values, even those
    //that are registered at run time and not loaded from a datapack
//    @ModifyArg(
//            method = "loadElementFromResource",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/WritableRegistry;register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lnet/minecraft/core/RegistrationInfo;)Lnet/minecraft/core/Holder$Reference;")
//    )
//    private static <T> T wover_loadElementFromResource(
//            ResourceKey<T> resourceKey,
//            T value,
//            RegistrationInfo registrationInfo
//    ) {
//        DatapackLoadElementImpl.didLoadFromDatapack(resourceKey, value);
//        return value;
//    }

}
