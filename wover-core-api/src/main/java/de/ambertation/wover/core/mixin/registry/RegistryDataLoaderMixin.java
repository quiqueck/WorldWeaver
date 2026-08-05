package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;
import de.ambertation.wover.core.impl.registry.DatapackRegistryLoadOrderImpl;
import de.ambertation.wover.entrypoint.LibWoverCore;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryValidator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
                LibWoverCore.C.log.debug("    - Adding " + key.identifier());
                enhanced.add(new RegistryDataLoader.RegistryData(key, codec, RegistryValidator.none()));
            }
        });

        wt_set_WORLDGEN_REGISTRIES(enhanced);
    }

    // Announce which registries a load consists of before its tasks are created, so
    // DatapackRegistryLoadOrder.awaitElements(...) has something to wait on no matter how early a
    // decoder asks. Both public overloads funnel into the same private one, but that takes a
    // package-private LoaderFactory we cannot name from here, so we hook them individually.
    //
    // This assumes loads do not overlap in time - true in practice, since every caller joins one
    // load before starting the next. If two ever did overlap, the second one's announcement would
    // replace the first one's futures and its waiters would fall through on the timeout, i.e. we
    // degrade to the unordered behaviour rather than deadlocking.
    @Inject(
            method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD")
    )
    private static void wover_beginResourceLoad(
            ResourceManager resourceManager,
            List<HolderLookup.RegistryLookup<?>> contextRegistries,
            List<RegistryDataLoader.RegistryData<?>> registriesToLoad,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        DatapackRegistryLoadOrderImpl.beginLoad(registriesToLoad);
    }

    @Inject(
            method = "load(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD")
    )
    private static void wover_beginNetworkLoad(
            Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> entries,
            ResourceProvider knownDataSource,
            List<HolderLookup.RegistryLookup<?>> contextRegistries,
            List<RegistryDataLoader.RegistryData<?>> registriesToLoad,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        DatapackRegistryLoadOrderImpl.beginLoad(registriesToLoad);
    }

    // In Minecraft 26.1.2 RegistryDataLoader.loadContentsFromManager was removed and registry
    // loading became asynchronous. The @Inject that used to live here (calling
    // DatapackRegistryBuilderImpl.bootstrap(...) right before a registry's tags are loaded) was
    // retargeted to the new per-registry load path in
    // ResourceManagerRegistryLoadTaskMixin.wover_bootstrap(...).

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
