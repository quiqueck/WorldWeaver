package org.betterx.wover.events.mixin.client.create_new_world_folder;

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
@Mixin(value = CreateWorldScreen.class, priority = 5000)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public abstract WorldCreationUiState getUiState();

    @Shadow
    private boolean recreated;

    //this is called when a new world is first created
    @Inject(method = "createNewWorldDirectory", at = @At("RETURN"))
    void wover_createNewWorld(CallbackInfoReturnable<Optional<LevelStorageSource.LevelStorageAccess>> cir) {
        WorldLifecycleImpl.CREATED_NEW_WORLD_FOLDER.emit(c -> c.init(
                        cir.getReturnValue().orElse(null),
                        this.getUiState().getSettings().worldgenLoadContext(),
                        this.getUiState().getWorldType().preset(),
                        this.getUiState().getSettings().selectedDimensions(),
                        this.recreated
                )
        );
    }
}
