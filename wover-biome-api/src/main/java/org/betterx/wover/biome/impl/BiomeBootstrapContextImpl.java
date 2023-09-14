package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.core.api.registry.CustomBootstrapContext;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import com.mojang.serialization.Lifecycle;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class BiomeBootstrapContextImpl extends CustomBootstrapContext<Biome, BiomeBootstrapContextImpl> implements BiomeBootstrapContext {
    private final List<BiomeBuilder<?>> registeredBuilders = new LinkedList<>();

    @Override
    public void register(@NotNull BiomeBuilder<?> builder, Lifecycle lifecycle) {
        registeredBuilders.add(builder);
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

    @Override
    public void onBootstrapContextChange(BiomeBootstrapContextImpl bootstrapContext) {
        WoverBiome.C.log.debug("Biome getter changed, resetting bootstrap context");
        BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA.emit(c -> c.bootstrap(bootstrapContext));
    }
}
