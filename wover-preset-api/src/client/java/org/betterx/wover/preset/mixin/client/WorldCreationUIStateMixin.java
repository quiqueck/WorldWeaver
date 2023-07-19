package org.betterx.wover.preset.mixin.client;

import org.betterx.wover.preset.impl.client.WorldPresetsClientImpl;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Environment(EnvType.CLIENT)
@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUIStateMixin {
    @Shadow
    public abstract WorldCreationUiState.WorldTypeEntry getWorldType();

    @Shadow
    public abstract void setWorldType(WorldCreationUiState.WorldTypeEntry worldTypeEntry);

    @Shadow
    @Final
    private List<WorldCreationUiState.WorldTypeEntry> normalPresetList;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wover_updateDimensions(
            Path path,
            WorldCreationContext worldCreationContext,
            Optional<ResourceKey<WorldPreset>> worldPreset,
            OptionalLong seed,
            CallbackInfo ci
    ) {
        //make sure the dimension selection is in sync with the initially selected
        //world preset
        worldPreset
                .flatMap(worldPresetResourceKey -> this.normalPresetList
                        .stream()
                        .filter(p -> p.preset() != null)
                        .filter(p -> p.preset().unwrapKey().isPresent())
                        .filter(p -> p.preset()
                                      .unwrapKey()
                                      .orElseThrow()
                                      .equals(worldPresetResourceKey))
                        .findAny()
                )
                .ifPresent(this::setWorldType);
    }

    @Inject(method = "getPresetEditor", at = @At("HEAD"), cancellable = true)
    private void wover_getPresetEditor(CallbackInfoReturnable<PresetEditor> cir) {
        //inject custom world preset screen
        final PresetEditor editor = WorldPresetsClientImpl.getSetupScreenForPreset(this.getWorldType().preset());
        if (editor != null) cir.setReturnValue(editor);
    }
}
