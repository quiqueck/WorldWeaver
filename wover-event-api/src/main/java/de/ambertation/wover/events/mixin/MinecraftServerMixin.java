package de.ambertation.wover.events.mixin;

import de.ambertation.wover.events.api.types.OnRegistryReady;
import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import com.mojang.datafixers.DataFixer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.Optional;

//priority needs to be low, to ensure that our modifications are applied before fabric
//otherwise other mods, that for example modify all nether biomes will generate a feature order cycle
//as those modification will be added after our features in custom nether biomes, but might be added to
//the vanilla biomes before our features are added.
@Mixin(value = MinecraftServer.class, priority = 150)
public class MinecraftServerMixin {
    @Shadow
    @Final
    private LayeredRegistryAccess<RegistryLayer> registries;

    @Shadow
    @Final
    protected LevelStorageSource.LevelStorageAccess storageSource;

    @Shadow
    @Final
    private PackRepository packRepository;

    @Shadow
    @Final
    protected WorldData worldData;

    /**
     * 26.2 appended a {@link NotificationManager} to the {@link MinecraftServer} constructor (it is the sink for
     * the new server notification services). A mixin handler for {@code <init>} has to mirror the constructor
     * descriptor exactly, so the parameter is repeated here even though we do not use it - without it the
     * injector would be rejected at mixin-apply time.
     */
    @Inject(at = @At("RETURN"), method = "<init>")
    private void wover_initMinecraftServerLate(
            Thread thread,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            WorldStem worldStem,
            Optional<GameRules> gameRules,
            Proxy proxy,
            DataFixer dataFixer,
            Services services,
            LevelLoadListener levelLoadListener,
            boolean bl,
            NotificationManager notificationManager,
            CallbackInfo ci
    ) {
        //in most cases this call is redundant, as we already captured the registries from the
        // world stem, but just in case...
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                worldStem.registries().compositeAccess(),
                OnRegistryReady.Stage.FINAL
        );
        //the same goes for the level storage access
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(levelStorageAccess);

        //this is the actual new event
        WorldLifecycleImpl.MINECRAFT_SERVER_READY.emit(c -> c.notify(
                levelStorageAccess,
                packRepository,
                worldStem
        ));
    }

    /**
     * We need a hook here to alter surface rules after Fabric did add its biomes
     * in {@link net.fabricmc.fabric.mixin.biome.modification.MinecraftServerMixin}
     */
    @Inject(method = "createLevels", at = @At(value = "HEAD"))
    private void wover_biomesReady(CallbackInfo ci) {
        //in most cases this call is redundant, as we already captured the registries from the
        // world stem, but just in case...
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(registries.compositeAccess(), OnRegistryReady.Stage.FINAL);
        //the same goes for the level storage access
        WorldLifecycleImpl.WORLD_FOLDER_READY.emit(storageSource);

        //this is the actual new event
        WorldLifecycleImpl.BEFORE_CREATING_LEVELS.emit(c -> c.notify(
                storageSource,
                packRepository,
                registries,
                worldData
        ));
    }
}
