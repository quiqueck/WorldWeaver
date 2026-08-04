package de.ambertation.wover.events.mixin.client.allow_experimental_warning_screen;

import de.ambertation.wover.events.impl.client.ClientWorldLifecycleImpl;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class)
public class WorldOpenFlowsMixin {
    @ModifyExpressionValue(
            method = "openWorldCheckWorldStemCompatibility",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;"
            )
    )
    public Lifecycle wt_noWarningScreen(Lifecycle original) {
        boolean bl = original != Lifecycle.stable();
        //TODO: 1.21 scheint nicht aufgerufen zu werden???
        if (!ClientWorldLifecycleImpl.ALLOW_EXPERIMENTAL_WARNING_SCREEN.process(bl))
            return Lifecycle.stable();

        return original;
    }
}
