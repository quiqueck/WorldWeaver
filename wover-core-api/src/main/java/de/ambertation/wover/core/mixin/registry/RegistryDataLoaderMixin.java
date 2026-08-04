package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;
import de.ambertation.wover.entrypoint.LibWoverCore;

import com.mojang.serialization.Decoder;
import net.minecraft.core.WritableRegistry;
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

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void wover_init(CallbackInfo ci) {
        List<RegistryDataLoader.RegistryData<?>> enhanced = new ArrayList<>(RegistryDataLoader.WORLDGEN_REGISTRIES.size() + 1);
        enhanced.addAll(RegistryDataLoader.WORLDGEN_REGISTRIES);
        LibWoverCore.C.log.debug("Adding custom WORLDGEN_REGISTRIES");
        DatapackRegistryBuilderImpl.forEach((key, codec) -> {
            if (codec != null) {
                LibWoverCore.C.log.debug("    - Adding " + key.location());
                enhanced.add(new RegistryDataLoader.RegistryData(key, codec, false));
            }
        });

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
