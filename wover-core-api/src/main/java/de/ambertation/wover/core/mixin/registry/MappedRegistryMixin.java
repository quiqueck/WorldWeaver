package de.ambertation.wover.core.mixin.registry;

import de.ambertation.wover.common.registry.api.CustomRegistryData;
import de.ambertation.wover.core.impl.registry.DatapackLoadElementImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin implements CustomRegistryData {
    private final Map<ResourceLocation, Object> wover$custom = new HashMap<>();

    public <T> @Nullable T wover_getData(@NotNull DataKey<T> id) {
        return (T) wover$custom.get(id.id);
    }

    public <T> @Nullable T wover_computeDataIfAbsent(
            @NotNull DataKey<T> id,
            @NotNull Function<ResourceLocation, T> fkt
    ) {
        return (T) wover$custom.computeIfAbsent(id.id, fkt);
    }

    public <T> void wover_putData(@NotNull DataKey<T> id, @Nullable T data) {
        wover$custom.put(id.id, data);
    }

    @Inject(method = "register", at = @At("HEAD"), cancellable = false)
    public <T> void wover_register(
            ResourceKey<T> resourceKey,
            T value,
            RegistrationInfo registrationInfo,
            CallbackInfoReturnable<Holder.Reference<T>> cir
    ) {
        if (value != null) {
            DatapackLoadElementImpl.didLoadFromDatapack(resourceKey, value);
        }
    }
}
