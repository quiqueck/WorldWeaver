package org.betterx.wover.events.mixin.world_registry;

import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.storage.WorldData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = WorldStem.class, priority = 100)
public class WorldStemMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Record;<init>()V", shift = At.Shift.AFTER))
    void wover_captureRegistry(
            CloseableResourceManager closeableResourceManager,
            ReloadableServerResources reloadableServerResources,
            LayeredRegistryAccess layeredRegistryAccess,
            WorldData worldData,
            CallbackInfo ci
    ) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                layeredRegistryAccess.compositeAccess(),
                OnRegistryReady.Stage.FINAL
        );
    }

}
