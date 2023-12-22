package org.betterx.wover.events.mixin;

import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Inject(method = "loadResources", at = @At("HEAD"))
    private static void wover_onLoadResources(
            ResourceManager resourceManager,
            RegistryAccess.Frozen frozen,
            FeatureFlagSet featureFlagSet,
            Commands.CommandSelection commandSelection,
            int i,
            Executor executor,
            Executor executor2,
            CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir
    ) {
        WorldLifecycleImpl.BEFORE_LOADING_RESOURCES.emit((c) -> c.didLoad(
                resourceManager,
                featureFlagSet
        ));
    }

}
