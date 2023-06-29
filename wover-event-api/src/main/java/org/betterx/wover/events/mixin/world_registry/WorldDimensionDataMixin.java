package org.betterx.wover.events.mixin.world_registry;


import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.WorldDimensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DedicatedServerProperties.WorldDimensionData.class)
public class WorldDimensionDataMixin {
    //this is called when a new world is first created on the server
    @Inject(method = "create", at = @At("HEAD"))
    void wover_captureRegistry(RegistryAccess registryAccess, CallbackInfoReturnable<WorldDimensions> cir) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(registryAccess, OnRegistryReady.Stage.LOADING);
    }


}
