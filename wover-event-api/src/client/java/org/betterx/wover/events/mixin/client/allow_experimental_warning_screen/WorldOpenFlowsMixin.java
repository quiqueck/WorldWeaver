package org.betterx.wover.events.mixin.client.allow_experimental_warning_screen;

import org.betterx.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class)
public class WorldOpenFlowsMixin {
    @ModifyArg(
            method = "checkForBackupAndLoad(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Ljava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;loadLevel(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lcom/mojang/serialization/Dynamic;ZZLjava/lang/Runnable;)V"
            ),
            index = 3
    )
    public boolean wt_noWarningScreen(boolean bl) {
        return ClientWorldLifecycleImpl.ALLOW_EXPERIMENTAL_WARNING_SCREEN.process(bl);
    }
}
