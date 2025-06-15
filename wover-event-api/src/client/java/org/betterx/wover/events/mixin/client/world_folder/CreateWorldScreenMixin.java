package org.betterx.wover.events.mixin.client.world_folder;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = CreateWorldScreen.class, priority = 4090)
public abstract class CreateWorldScreenMixin {
    @ModifyArg(method = "createNewWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;createLevelFromExistingSettings(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/ReloadableServerResources;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/level/storage/WorldData;)V"))
    private LevelStorageSource.LevelStorageAccess wover_captureStorage(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        //called when a new world is created on the client
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(levelStorageAccess);
        return levelStorageAccess;
    }
}
