package org.betterx.wover.events.mixin.resource_manager;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void wover_reloadResourcesEvent(
            Collection<String> collection,
            CallbackInfoReturnable<CompletableFuture<Void>> cir
    ) {
        cir.getReturnValue().handleAsync((value, throwable) -> {
            if (throwable == null) {
                final ResourceManager rm = resources.resourceManager();
                WorldLifecycleImpl.RESOURCES_LOADED.emit(rm);
            }
            return value;
        }, (MinecraftServer) (Object) this);
    }
}
