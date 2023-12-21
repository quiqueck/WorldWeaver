package org.betterx.wover.events.mixin.world_folder;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.server.Main;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Main.class)
public class MainMixin {
    @ModifyExpressionValue(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/LevelStorageSource;validateAndCreateAccess(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;"
            )
    )
    private static LevelStorageSource.LevelStorageAccess wover_captureStorageAccess(LevelStorageSource.LevelStorageAccess storageAccess) {
        //called when a world is loaded/created on the server
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(storageAccess);

        return storageAccess;
    }
}
