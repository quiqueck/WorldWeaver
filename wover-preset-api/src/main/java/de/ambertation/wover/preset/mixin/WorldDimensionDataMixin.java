package de.ambertation.wover.preset.mixin;

import de.ambertation.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DedicatedServerProperties.WorldDimensionData.class)
public class WorldDimensionDataMixin {
    //Make sure Servers use our Default World Preset
    @ModifyArg(
            method = "create",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderLookup;get(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;")
    )
    private ResourceKey<WorldPreset> wover_returnDefault(ResourceKey<WorldPreset> resourceKey) {
        return WorldPresetsManagerImpl.getDefault();
    }
}
