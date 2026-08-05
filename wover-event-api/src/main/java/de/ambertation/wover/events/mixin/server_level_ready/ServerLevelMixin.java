package de.ambertation.wover.events.mixin.server_level_ready;

import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(value = ServerLevel.class, priority = 2000)
public class ServerLevelMixin {
    @Inject(method = "<init>*", at = @At("TAIL"))
    void wover_onServerLevelInit(
            MinecraftServer minecraftServer,
            Executor executor,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey<Level> resourceKey,
            LevelStem levelStem,
            boolean bl,
            long seed,
            List list,
            boolean bl2,
            CallbackInfo ci
    ) {
        WorldLifecycleImpl.SERVER_LEVEL_READY.emit((call) -> call.notify((ServerLevel) (Object) this, resourceKey, levelStem, seed));
    }
}
