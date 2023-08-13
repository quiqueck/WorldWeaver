package org.betterx.wover.biome.api.builder;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.NotNull;

public interface BiomeBootstrapContext {
    void register(@NotNull BiomeBuilder<?> builder, Lifecycle lifecycle);
    void register(@NotNull BiomeBuilder<?> builder);
    <S> HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey);
}
