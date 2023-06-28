package org.betterx.wover.events.mixin;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.world.level.storage.LevelStorageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(value = LevelStorageSource.LevelStorageAccess.class, priority = 5000)
public class LevelStorageAccessMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    void wover_worldFolderReady(LevelStorageSource source, String levelId, Path path, CallbackInfo ci) {
        WoverEventMod.C.LOG.debug("wover_worldFolderReady: " + levelId + " " + path);
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit((LevelStorageSource.LevelStorageAccess) (Object) this);
    }
}
