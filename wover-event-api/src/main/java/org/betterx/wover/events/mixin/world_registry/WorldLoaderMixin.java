package org.betterx.wover.events.mixin.world_registry;

import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    //this is the place a new Registry access gets first istantiated
    //either when a new Datapack was added to a world on the create-screen
    //or because we did start world loading
    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess$Frozen;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static RegistryAccess.Frozen wover_captureRegistry(RegistryAccess.Frozen frozen) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(frozen, OnRegistryReady.Stage.PREPARATION);
        return frozen;
    }
}