package de.ambertation.wover.block.mixin.client;

import de.ambertation.wover.block.impl.client.render.ClientBlockTintBootstrap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.main.GameConfig;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies every block's {@code TintBinding} to vanilla's {@link BlockColors} once it exists.
 * <p>
 * {@code BlockColors} is built inside {@code Minecraft}'s constructor and only reachable from there, so this is
 * the earliest (and only) place the in-world half of a tint binding can be registered.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Final
    @Shadow
    private BlockColors blockColors;

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void wover_applyBlockTints(GameConfig args, CallbackInfo info) {
        ClientBlockTintBootstrap.applyBlockTints(blockColors);
    }
}
