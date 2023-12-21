package org.betterx.wover.events.mixin.client.world_folder;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class, priority = 20)
public abstract class WorldOpenFlowsMixin {
    @ModifyExpressionValue(
            method = "checkForBackupAndLoad(Ljava/lang/String;Ljava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;createWorldAccess(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;")
    )
    private LevelStorageSource.LevelStorageAccess wover_captureStorage(
            LevelStorageSource.LevelStorageAccess levelStorageAccess
    ) {
        //called when a world is loaded on the client
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(levelStorageAccess);

        return levelStorageAccess;
    }
}
