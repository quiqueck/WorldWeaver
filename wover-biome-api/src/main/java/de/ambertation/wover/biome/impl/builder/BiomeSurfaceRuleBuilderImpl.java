package de.ambertation.wover.biome.impl.builder;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.builder.BiomeSurfaceRuleBuilder;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.surface.impl.SurfaceRuleBuilderImpl;
import de.ambertation.wover.surface.impl.SurfaceRuleRegistryImpl;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.NotNull;

public class BiomeSurfaceRuleBuilderImpl<B extends BiomeBuilder<B>> extends SurfaceRuleBuilderImpl<BiomeSurfaceRuleBuilder<B>> implements BiomeSurfaceRuleBuilder<B> {
    private final B sourceBuilder;

    public BiomeSurfaceRuleBuilderImpl(BiomeKey<?> biomeKey, B sourceBuilder) {
        super();
        this.biome(biomeKey.key);
        this.sourceBuilder = sourceBuilder;
    }

    public void register(@NotNull BootstrapContext<AssignedSurfaceRule> ctx) {
        final ResourceKey<AssignedSurfaceRule> ruleKey = SurfaceRuleRegistry.createKey(this.biomeKey.identifier());
        SurfaceRuleRegistryImpl.register(ctx, ruleKey, biomeKey, getRuleSource(), sortPriority);
    }

    public B finishSurface() {
        return sourceBuilder;
    }
}
