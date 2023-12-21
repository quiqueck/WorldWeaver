package org.betterx.wover.events.mixin.client.before_client_load;

import org.betterx.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class, priority = 10)
public abstract class WorldOpenFlowsMixin {
    @WrapOperation(
            method = "checkForBackupAndLoad(Ljava/lang/String;Ljava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;checkForBackupAndLoad(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Ljava/lang/Runnable;)V"
            )
    )
    private void wover_beforeLoadLevel(
            WorldOpenFlows instance,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            Runnable runnable,
            Operation<Void> original
    ) {
        ClientWorldLifecycleImpl.BEFORE_CLIENT_LOAD_SCREEN.process(
                levelStorageAccess,
                () -> original.call(instance, levelStorageAccess, runnable)
        );
    }
}
