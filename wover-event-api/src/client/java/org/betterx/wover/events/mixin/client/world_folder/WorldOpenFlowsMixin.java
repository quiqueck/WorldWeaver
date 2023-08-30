package org.betterx.wover.events.mixin.client.world_folder;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class, priority = 20)
public abstract class WorldOpenFlowsMixin {
    @Inject(method = "loadWorldStem(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;ZLnet/minecraft/server/packs/repository/PackRepository;)Lnet/minecraft/server/WorldStem;", at = @At("HEAD"))
    private void wover_captureStorage(
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            boolean bl,
            PackRepository packRepository,
            CallbackInfoReturnable<WorldStem> cir
    ) {
        //called when a world is loaded on the client
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(levelStorageAccess);
    }
}
