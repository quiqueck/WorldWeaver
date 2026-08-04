package de.ambertation.wover.events.mixin.client.create_new_world_folder;

import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(EnvType.CLIENT)
@Mixin(value = CreateWorldScreen.class, priority = 5000)
public abstract class CreateWorldScreenMixin {
    @Shadow
    public abstract WorldCreationUiState getUiState();

    @Shadow
    private boolean recreated;

    //this is called when a new world is first created
    @ModifyArg(method = "createNewWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/WorldOpenFlows;createLevelFromExistingSettings(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/ReloadableServerResources;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/level/storage/WorldData;)V"))
    LevelStorageSource.LevelStorageAccess wover_createNewWorld(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        WorldLifecycleImpl.CREATED_NEW_WORLD_FOLDER.emit(c -> c.init(
                        levelStorageAccess,
                        this.getUiState().getSettings().worldgenLoadContext(),
                        this.getUiState().getWorldType().preset(),
                        this.getUiState().getSettings().selectedDimensions(),
                        this.recreated
                )
        );
        return levelStorageAccess;
    }
}
