package org.betterx.wover.events.mixin;

import org.betterx.wover.WoverEventMod;

import com.mojang.datafixers.DataFixer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(value = MinecraftServer.class, priority = 2000)
public class MinecraftServerMixin {
    @Shadow
    @Final
    private LayeredRegistryAccess<RegistryLayer> registries;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void wover_initMinecraftServerEarly(
            Thread thread,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Proxy proxy,
            DataFixer dataFixer,
            Services services,
            ChunkProgressListenerFactory chunkProgressListenerFactory,
            CallbackInfo ci
    ) {
        WoverEventMod.C.LOG.debug("wover_initMinecraftServerEarly: " + worldStem.registries());
    }

    @Inject(method = "createLevels", at = @At(value = "HEAD"))
    private void wover_initMinecraftServerLate(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        WoverEventMod.C.LOG.debug("wover_initMinecraftServerLate: " + registries);
    }
}
