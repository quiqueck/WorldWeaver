package org.betterx.wover.client.events.mixin;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.client.events.impl.ClientWorldLifecycleImpl;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldOpenFlows.class, priority = 10)
public abstract class WorldOpenFlowsMixin {

    @Shadow
    @Final
    private LevelStorageSource levelSource;

    @Shadow
    protected abstract void doLoadLevel(Screen screen, String levelID, boolean safeMode, boolean canAskForBackup);

    @Inject(method = "loadLevel", cancellable = true, at = @At("HEAD"))
    private void wover_callFixerOnLoad(Screen screen, String levelId, CallbackInfo ci) {
        WoverEventMod.C.LOG.debug("wover_callFixerOnLoad: " + levelId);
        if (ClientWorldLifecycleImpl.BEFORE_CLIENT_LOAD_SCREEN.process(levelSource, levelId, () -> {
            this.doLoadLevel(screen, levelId, false, false);
        })) {
            ci.cancel();
        }
    }
}
