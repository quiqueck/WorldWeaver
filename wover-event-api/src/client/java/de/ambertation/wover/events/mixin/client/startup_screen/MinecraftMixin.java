package de.ambertation.wover.events.mixin.client.startup_screen;

import de.ambertation.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(
            method = "addInitialScreens",
            at = @At("TAIL")
    )
    private void wover_onScreenList(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> cir) {
        ClientWorldLifecycleImpl.ENUMERATE_STARTUP_SCREENS.process(list);
    }
}
