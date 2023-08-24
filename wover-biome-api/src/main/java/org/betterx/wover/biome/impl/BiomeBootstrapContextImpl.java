package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class BiomeBootstrapContextImpl implements BiomeBootstrapContext {
    private final List<BiomeBuilder<?>> registeredBuilders = new LinkedList<>();
    private BootstapContext<?> lookupContext;

    @ApiStatus.Internal
    public final void setLookupContext(BootstapContext<?> lookupContext) {
        this.lookupContext = lookupContext;
    }

    @Override
    public void register(@NotNull BiomeBuilder<?> builder, Lifecycle lifecycle) {
        registeredBuilders.add(builder);
    }

    @Override
    public void register(@NotNull BiomeBuilder<?> builder) {
        this.register(builder, Lifecycle.stable());
    }

    @Override
    public <S> HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey) {
        if (lookupContext == null) return null;
        return lookupContext.lookup(registryKey);
    }

    @ApiStatus.Internal
    public final void bootstrapBiome(BootstapContext<Biome> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerBiome(context);
        }
    }

    @ApiStatus.Internal
    public final void bootstrapBiomeData(BootstapContext<BiomeData> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerBiomeData(context);
        }
    }

    @ApiStatus.Internal
    public final void bootstrapSurfaceRules(BootstapContext<AssignedSurfaceRule> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerSurfaceRule(context);
        }
    }

    public final void prepareTags(TagBootstrapContext<Biome> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerBiomeTags(context);
        }
    }
}
