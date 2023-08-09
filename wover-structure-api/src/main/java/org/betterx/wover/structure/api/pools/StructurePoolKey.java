package org.betterx.wover.structure.api.pools;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import org.jetbrains.annotations.NotNull;

public class StructurePoolKey {
    /**
     * The key for the {@link StructureTemplatePool} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureTemplatePool> key;

    public StructurePoolBuilder bootstrap(@NotNull BootstapContext<StructureTemplatePool> context) {
        return new StructurePoolBuilder(key, context);
    }

    StructurePoolKey(@NotNull ResourceKey<StructureTemplatePool> key) {
        this.key = key;
    }
}
