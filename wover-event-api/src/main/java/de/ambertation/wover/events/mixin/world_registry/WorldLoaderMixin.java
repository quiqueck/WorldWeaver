package de.ambertation.wover.events.mixin.world_registry;

import de.ambertation.wover.events.api.types.OnRegistryReady;
import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @ModifyArg(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
                    ordinal = 1)
    )
    private static <S> ResourceManager wover_captureRegistryPairSecond(
            ResourceManager resourceManager,
            List<HolderLookup.RegistryLookup<?>> list,
            List<RegistryDataLoader.RegistryData<?>> list2
    ) {
        // this is called when a new world is first created on the server
        // we generate a temporary RegistryAccess here, as it is no longer generated in the WorldLoader.load method
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess = RegistryLayer.createRegistryAccess();
        LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess2 = layeredRegistryAccess.replaceFrom(
                RegistryLayer.WORLDGEN,
                RegistryDataLoader.load(resourceManager, list, RegistryDataLoader.WORLDGEN_REGISTRIES)
        );
        RegistryAccess.Frozen frozen = layeredRegistryAccess2.getAccessForLoading(RegistryLayer.DIMENSIONS);

        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(frozen, OnRegistryReady.Stage.LOADING);
        return resourceManager;
    }

    //this is the place a new Registry access gets first instantiated
    //either when a new Datapack was added to a world on the create-screen
    //or because we did start world loading
    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/util/List;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static LayeredRegistryAccess<RegistryLayer> wover_captureRegistry(LayeredRegistryAccess<RegistryLayer> layered) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                layered.getAccessForLoading(RegistryLayer.RELOADABLE),
                OnRegistryReady.Stage.PREPARATION
        );
        return layered;
    }
}