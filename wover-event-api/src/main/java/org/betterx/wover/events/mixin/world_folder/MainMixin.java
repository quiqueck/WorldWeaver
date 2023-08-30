package org.betterx.wover.events.mixin.world_folder;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.server.Main;
import net.minecraft.world.level.storage.LevelStorageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Main.class)
public class MainMixin {
    @ModifyVariable(method = "main", remap = false, at = @At(value = "STORE"), name = "levelStorageAccess", ordinal = 0)
    private static LevelStorageSource.LevelStorageAccess wover_captureStorageAccess(LevelStorageSource.LevelStorageAccess storageAccess) {
        //called when a world is loaded/created on the server
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(storageAccess);

        return storageAccess;
    }
}
