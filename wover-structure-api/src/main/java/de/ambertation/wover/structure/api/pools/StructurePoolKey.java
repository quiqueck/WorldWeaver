package de.ambertation.wover.structure.api.pools;

import de.ambertation.wover.structure.impl.pools.StructurePoolBuilderImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around {@link ResourceKey} that identifies a {@link StructureTemplatePool}. Create one with
 * {@link StructurePoolManager#createKey(ResourceLocation)} (or the {@link de.ambertation.wover.structure.api.StructureKeys#pool(ResourceLocation)}
 * alias), then call {@link #bootstrap(BootstrapContext)} to start building the pool.
 */
public class StructurePoolKey {
    /**
     * The key for the {@link StructureTemplatePool} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureTemplatePool> key;

    /**
     * Creates a {@link StructurePoolBuilder} to build and register the {@link StructureTemplatePool} for
     * this key with the given {@link BootstrapContext}.
     *
     * @param context The bootstrap context to register the pool with
     * @return The builder
     */
    public StructurePoolBuilder bootstrap(@NotNull BootstrapContext<StructureTemplatePool> context) {
        return new StructurePoolBuilderImpl(key, context);
    }

    StructurePoolKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.TEMPLATE_POOL, location);
    }
}
