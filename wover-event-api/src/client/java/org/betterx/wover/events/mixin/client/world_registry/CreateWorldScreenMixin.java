package org.betterx.wover.events.mixin.client.world_registry;

import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(value = CreateWorldScreen.class, priority = 4095)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public abstract WorldCreationUiState getUiState();

    @Inject(method = "createNewWorldDirectory", at = @At("RETURN"))
    void wover_captureRegistry(CallbackInfoReturnable<Optional<LevelStorageSource.LevelStorageAccess>> cir) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                this.getUiState()
                    .getSettings()
                    .worldgenLoadContext(),
                OnRegistryReady.Stage.LOADING
        );
    }
}
