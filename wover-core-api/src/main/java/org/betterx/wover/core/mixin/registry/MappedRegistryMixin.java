package org.betterx.wover.core.mixin.registry;

import org.betterx.wover.common.registry.api.CustomRegistryData;

import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;

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
}
