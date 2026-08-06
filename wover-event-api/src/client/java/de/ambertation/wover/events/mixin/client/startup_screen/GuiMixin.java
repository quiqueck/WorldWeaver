package de.ambertation.wover.events.mixin.client.startup_screen;

import de.ambertation.wover.events.impl.client.ClientWorldLifecycleImpl;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

/**
 * 26.2 moved the whole screen/overlay stack off {@link net.minecraft.client.Minecraft} and onto
 * {@link Gui} (the same refactor that turned {@code Minecraft#setScreen} into {@code Minecraft#gui#setScreen}).
 * {@code buildInitialScreens} and the private {@code addInitialScreens(List)} moved with it, so this mixin
 * had to be retargeted from {@code Minecraft} to {@code Gui}; it used to live in a {@code MinecraftMixin}
 * in this package.
 * <p>
 * Nothing catches this at compile time - the mixin still compiles against {@code Minecraft}, and only
 * fails when it is applied, with "could not find any targets matching 'addInitialScreens'".
 * <p>
 * The method body itself is unchanged - it still collects
 * {@code Function<Runnable, Screen>} factories into the supplied list and returns a boolean, and it still has
 * exactly one RETURN, so injecting at TAIL appends our screens after all vanilla ones (which is the contract
 * documented on {@code ClientWorldLifecycle#ENUMERATE_STARTUP_SCREENS}).
 */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(
            method = "addInitialScreens",
            at = @At("TAIL")
    )
    private void wover_onScreenList(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> cir) {
        ClientWorldLifecycleImpl.ENUMERATE_STARTUP_SCREENS.process(list);
    }
}
