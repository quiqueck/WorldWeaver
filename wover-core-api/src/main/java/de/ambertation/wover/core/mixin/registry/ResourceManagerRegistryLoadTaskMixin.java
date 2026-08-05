package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryBuilderImpl;
import de.ambertation.wover.core.impl.registry.DatapackRegistryLoadOrderImpl;

import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceManagerRegistryLoadTask;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// In Minecraft 26.1.2 registry loading became asynchronous. The old
// RegistryDataLoader.loadContentsFromManager was removed and the per-registry
// content loading now lives in ResourceManagerRegistryLoadTask.load(...).
//
// We preserve the exact old behaviour: for every worldgen registry loaded from
// datapacks we call DatapackRegistryBuilderImpl.bootstrap(...) once, right
// before that registry's tags are loaded (the INVOKE of
// TagLoader.loadTagsForRegistry(...)). That tag-loading happens inside the
// asynchronous continuation lambda (lambda$load$3), which does not have access
// to the RegistryOps.RegistryInfoLookup anymore, so we stash it from the head
// of load(...) into a per-task field and read it back in the lambda.
@Mixin(ResourceManagerRegistryLoadTask.class)
public abstract class ResourceManagerRegistryLoadTaskMixin<T> {
    @Unique
    private volatile RegistryOps.RegistryInfoLookup wover_registryInfoLookup;

    @Inject(method = "load", at = @At("HEAD"))
    private void wover_captureContext(
            RegistryOps.RegistryInfoLookup context,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        this.wover_registryInfoLookup = context;
    }

    // Safety net for DatapackRegistryLoadOrder: wover_bootstrap below is the point where a registry
    // is really done registering elements, but it never runs if the task fails earlier (a broken
    // JSON, a missing resource). Releasing waiters on the task's own completion as well means a
    // failing load surfaces as its actual error instead of as everyone else timing out on it.
    @Inject(method = "load", at = @At("RETURN"))
    private void wover_releaseOnCompletion(
            RegistryOps.RegistryInfoLookup context,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<?>> cir
    ) {
        final var key = ((RegistryLoadTaskAccessor) (Object) this).wover_getRegistry().key();
        cir.getReturnValue().whenComplete((result, error) -> DatapackRegistryLoadOrderImpl.markElementsRegistered(key));
    }

    @Inject(
            method = "lambda$load$3",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/tags/TagLoader;loadTagsForRegistry(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/tags/TagLoader$ElementLookup;)Ljava/util/Map;",
                    shift = At.Shift.BEFORE
            )
    )
    private void wover_bootstrap(Map<?, ?> loadedEntries, CallbackInfo ci) {
        // The writable registry lives as a private field in the RegistryLoadTask super class;
        // we read it through an accessor mixin. registerElements(...) has already run at this
        // point, so this is the registry we register the custom bootstrap entries into.
        @SuppressWarnings("unchecked")
        WritableRegistry<T> writableRegistry =
                (WritableRegistry<T>) ((RegistryLoadTaskAccessor) (Object) this).wover_getRegistry();
        DatapackRegistryBuilderImpl.bootstrap(
                this.wover_registryInfoLookup,
                writableRegistry.key(),
                writableRegistry
        );

        // Everything this registry will ever contain is in place now - datapack entries from
        // registerElements(...) plus the bootstrap entries above - so anyone who declared a
        // dependency on it through DatapackRegistryLoadOrder can proceed.
        DatapackRegistryLoadOrderImpl.markElementsRegistered(writableRegistry.key());
    }
}
