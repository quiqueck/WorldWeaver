package org.betterx.wover.structure.api.pools;

import org.betterx.wover.structure.impl.pools.StructurePoolBuilderImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import org.jetbrains.annotations.NotNull;

public class StructurePoolKey {
    /**
     * The key for the {@link StructureTemplatePool} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureTemplatePool> key;

    public StructurePoolBuilder bootstrap(@NotNull BootstapContext<StructureTemplatePool> context) {
        return new StructurePoolBuilderImpl(key, context);
    }

    StructurePoolKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.TEMPLATE_POOL, location);
    }
}
