package org.betterx.wover.preset.mixin;

import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DedicatedServerProperties.WorldDimensionData.class)
public class WorldDimensionDataMixin {
    //Make sure Servers use our Default World Preset
    @ModifyArg(method = "create", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/core/Registry;getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private ResourceKey<WorldPreset> wover_returnDefault(ResourceKey<WorldPreset> resourceKey) {
        return WorldPresetsManagerImpl.getDefault();
    }
}
