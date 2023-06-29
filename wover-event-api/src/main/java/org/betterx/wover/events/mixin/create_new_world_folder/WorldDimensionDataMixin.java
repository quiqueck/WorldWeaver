package org.betterx.wover.events.mixin.create_new_world_folder;


import org.betterx.wover.events.impl.WorldLifecycleImpl;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@Mixin(DedicatedServerProperties.WorldDimensionData.class)
public class WorldDimensionDataMixin {
    @Shadow
    @Final
    private String levelType;

    @Shadow
    @Final
    private static Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

    //this is called when a new world is first created on the server
    @Inject(method = "create", at = @At("RETURN"))
    void wover_onCreateWorld(RegistryAccess registryAccess, CallbackInfoReturnable<WorldDimensions> cir) {
        Holder<WorldPreset> holder = wover_getWorldPreset(registryAccess);
        WorldDimensions dimensions = cir.getReturnValue();

        WorldLifecycleImpl.CREATED_NEW_WORLD_FOLDER.emit(c -> c.init(
                        WorldState.storageAccess(),
                        registryAccess,
                        holder,
                        dimensions,
                        false
                )
        );
    }

    @NotNull
    private Holder<WorldPreset> wover_getWorldPreset(RegistryAccess registryAccess) {
        Registry<WorldPreset> worldPresetRegistry = registryAccess.registryOrThrow(Registries.WORLD_PRESET);
        Holder.Reference<WorldPreset> reference = worldPresetRegistry.getHolder(WorldPresets.NORMAL)
                                                                     .or(() -> worldPresetRegistry.holders().findAny())
                                                                     .orElseThrow();

        Optional<ResourceKey<WorldPreset>> presetKey = Optional
                .ofNullable(ResourceLocation.tryParse(this.levelType))
                .map((resourceLocation) -> ResourceKey.create(
                        Registries.WORLD_PRESET,
                        resourceLocation
                ))
                .or(() -> Optional.ofNullable(LEGACY_PRESET_NAMES.get(this.levelType)));
        Objects.requireNonNull(worldPresetRegistry);
        Holder<WorldPreset> holder = presetKey
                .flatMap(worldPresetRegistry::getHolder)
                .orElse(reference);
        return holder;
    }


}
