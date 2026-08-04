package de.ambertation.wover.biome.impl;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.core.api.registry.CustomBootstrapContext;
import de.ambertation.wover.entrypoint.LibWoverBiome;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import com.mojang.serialization.Lifecycle;
import net.minecraft.data.worldgen.BootstrapContext;
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
    public final void bootstrapBiome(BootstrapContext<Biome> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerBiome(context);
        }
    }

    @ApiStatus.Internal
    public final void bootstrapBiomeData(BootstrapContext<BiomeData> context) {
        for (BiomeBuilder<?> builder : registeredBuilders) {
            builder.registerBiomeData(context);
        }
    }

    @ApiStatus.Internal
    public final void bootstrapSurfaceRules(BootstrapContext<AssignedSurfaceRule> context) {
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
        LibWoverBiome.C.log.debug("Biome getter changed, resetting bootstrap context");
        BiomeManagerImpl.BOOTSTRAP_BIOMES_WITH_DATA.emit(c -> c.bootstrap(bootstrapContext));
    }
}
